package org.pentaho.platform.engine.security.multiple.auth;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.api.mt.ITenant;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.multiple.auth.holder.Provider;
import org.pentaho.platform.engine.security.multiple.auth.holder.ProviderHolder;
import org.springframework.util.Assert;

public class MultipleAuthUserRoleListService implements IUserRoleListService {

  private static final Log logger = LogFactory.getLog( MultipleAuthUserDetailsService.class );

  private ProviderHolder holder;

  public MultipleAuthUserRoleListService() {
    super();
  }

  @Override
  public List<String> getAllRoles() {
    return getAllRoles( null );
  }

  @Override
  public List<String> getAllRoles( ITenant tenant ) {
    return getRolesForUser( tenant, null );
  }

  @Override
  public List<String> getRolesForUser( ITenant tenant, String user ) {

    List<String> roles = null;

    for ( Provider provider : getProviders() ) {

      logger.info( "Attempting to load roles for user via " + provider.getId() );

      if ( !CollectionUtils.isEmpty( roles = provider.getUserRoleListService().getRolesForUser( tenant, user ) ) ) {

        logger.info( "user roles fetched via " + provider.getId() );
        return roles;
      }
    }

    logger.warn( "user roles not found" );
    return null;
  }

  @Override
  public List<String> getSystemRoles() {

    List<String> systemRoles = null;

    for ( Provider provider : getProviders() ) {

      logger.info( "Attempting to load system roles via " + provider.getId() );

      if ( !CollectionUtils.isEmpty( systemRoles = provider.getUserRoleListService().getSystemRoles() ) ) {

        logger.info( "system roles fetched via " + provider.getId() );
        return systemRoles;
      }
    }

    logger.warn( "system roles not found" );
    return null;
  }

  @Override
  public List<String> getAllUsers() {
    return getAllUsers( null );
  }

  @Override
  public List<String> getAllUsers( ITenant tenant ) {
    return getUsersInRole( tenant, null );
  }

  @Override
  public List<String> getUsersInRole( ITenant tenant, String role ) {

    List<String> users = null;

    for ( Provider provider : getProviders() ) {

      logger.info( "Attempting to load users for role via " + provider.getId() );

      if ( !CollectionUtils.isEmpty( users = provider.getUserRoleListService().getUsersInRole( tenant, role ) ) ) {

        logger.info( "users fetched via " + provider.getId() );
        return users;
      }
    }

    logger.warn( "users not found" );
    return null;
  }

  public ProviderHolder getHolder() {
    return holder;
  }

  public void setHolder( ProviderHolder holder ) {
    Assert.notNull( holder );
    this.holder = holder;
  }

  private List<Provider> getProviders() {

    if ( userHasProviderSetForHim() ) {

      String providerId = (String) PentahoSessionHolder.getSession().getAttribute( Provider.MULTIPLE_AUTH_PROVIDER );

      if ( getHolder().hasProvider( providerId ) ) {

        return Arrays.asList( new Provider[] { getHolder().getProvider( providerId ) } );
      }
    }

    return getHolder().getProviders();
  }

  private boolean userHasProviderSetForHim() {
    return PentahoSessionHolder.getSession() != null
        && !StringUtils.isEmpty( (String) PentahoSessionHolder.getSession().getAttribute(
            Provider.MULTIPLE_AUTH_PROVIDER ) );
  }
}
