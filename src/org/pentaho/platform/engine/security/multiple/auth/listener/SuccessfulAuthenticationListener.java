package org.pentaho.platform.engine.security.multiple.auth.listener;

import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.api.engine.ISystemConfig;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.multiple.auth.holder.Provider;
import org.pentaho.platform.engine.security.multiple.auth.holder.ProviderHolder;
import org.pentaho.platform.plugin.services.security.userrole.PentahoCachingUserDetailsService;
import org.pentaho.platform.repository2.unified.jcr.JcrTenantUtils;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.security.event.authentication.AuthenticationSuccessEvent;
import org.springframework.security.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

public class SuccessfulAuthenticationListener implements ApplicationListener, Ordered {

  private final int order = 101; //runs after PentahoAuthenticationSuccessListener

  public SuccessfulAuthenticationListener() {
    super();
  }

  @Override
  public int getOrder() {
    return order;
  }

  @Override
  public void onApplicationEvent( ApplicationEvent event ) {

    if ( isAuthenticationSuccessEvent( event ) && isMultipleAuthSecurityProviderEnabled() ) {

      String providerId = getProviderIdForUser( PentahoSessionHolder.getSession().getName() );

      if ( !StringUtils.isEmpty( providerId ) ) {

        // this user's session will declare this provider as the one to use for all user/role data fetching
        PentahoSessionHolder.getSession().setAttribute( Provider.MULTIPLE_AUTH_PROVIDER, providerId );
        
        // update pentaho user cache: prepare it to receive this user
        preparePentahoUserCacheToStoreUser( PentahoSessionHolder.getSession().getName() );
      }
    }
  }

  private boolean isAuthenticationSuccessEvent( ApplicationEvent event ) {
    return event != null
        && ( event instanceof AuthenticationSuccessEvent || event instanceof InteractiveAuthenticationSuccessEvent );
  }

  private boolean isMultipleAuthSecurityProviderEnabled() {

    String securityProvider = StringUtils.EMPTY;

    try {
      securityProvider =
          ( (ISystemConfig) PentahoSystem.get( ISystemConfig.class, PentahoSessionHolder.getSession() ) )
              .getConfiguration( "security" ).getProperties().get( "provider" ).toString();

    } catch ( Throwable t ) {
      // do nothing
    }

    return "multiple".equalsIgnoreCase( securityProvider );
  }

  public String getProviderIdForUser( String username ) {

    ProviderHolder holder = PentahoSystem.get( ProviderHolder.class, "holder", PentahoSessionHolder.getSession() );

    if ( holder != null && holder.getProviders() != null ) {

      for ( Provider provider : holder.getProviders() ) {

        try {

          if ( ( provider.getUserDetailsService().loadUserByUsername( username ) ) != null ) {
            return provider.getId();
          }

        } catch ( UsernameNotFoundException e ) {
          // continue searching
        }
      }
    }
    return null;
  }
  
  private void preparePentahoUserCacheToStoreUser( String username ){
    
    if( !StringUtils.isEmpty( username ) ){
      
      UserDetailsService service = PentahoSystem.get( UserDetailsService.class, PentahoSessionHolder.getSession() );
      
      if( service != null && service instanceof PentahoCachingUserDetailsService ){
        
        if( ( ( PentahoCachingUserDetailsService ) service ).getUserCache() != null ){
          
          ( ( PentahoCachingUserDetailsService ) service ).getUserCache().removeUserFromCache( username );
          ( ( PentahoCachingUserDetailsService ) service ).getUserCache().removeUserFromCache( JcrTenantUtils.getTenantedUser( username ) );
        }
      }
    }
  }
}
