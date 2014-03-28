package org.pentaho.platform.engine.security.multiple.auth;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.ILogoutListener;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.multiple.auth.holder.Provider;
import org.pentaho.platform.engine.security.multiple.auth.holder.ProviderHolder;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.DaoAuthenticationProvider;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.util.Assert;

public class MultipleAuthenticationProvider extends DaoAuthenticationProvider {

  private static final Log logger = LogFactory.getLog( MultipleAuthenticationProvider.class );

  private ProviderHolder holder;

  public MultipleAuthenticationProvider() {
    super();

    // creating a logoutListener at runtime that clears the user/provider association when user logs out
    PentahoSystem.addLogoutListener( new ILogoutListener() {

      @Override
      public void onLogout( IPentahoSession pentahoSession ) {
        pentahoSession.removeAttribute( Provider.MULTIPLE_AUTH_PROVIDER );
      }
    } );
  }

  @Override
  protected void additionalAuthenticationChecks( UserDetails userDetails,
      UsernamePasswordAuthenticationToken authentication ) throws AuthenticationException {

    String providerId =
        ( (MultipleAuthUserDetailsService) getUserDetailsService() ).getProviderIdForUser( userDetails.getUsername() );

    Provider provider = holder.getProvider( providerId );

    if ( provider != null && provider.getPasswordEncoder() != null ) {

      // set encoder for validating login credentials
      setPasswordEncoder( holder.getProvider( providerId ).getPasswordEncoder() );

    } else {
      logger.warn( "A password encoder for provider '" + providerId
          + "' was not set; login credentials validator will use spring's default PlaintextPasswordEncoder" );
    }

    // carry on with spring's authentication flow
    super.additionalAuthenticationChecks( userDetails, authentication );
  }

  public ProviderHolder getHolder() {
    return holder;
  }

  public void setHolder( ProviderHolder holder ) {
    Assert.notNull( holder );
    this.holder = holder;
  }
}
