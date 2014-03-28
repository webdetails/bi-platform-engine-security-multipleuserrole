package org.pentaho.platform.engine.security.multiple.auth.holder;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.api.engine.IUserRoleListService;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.userdetails.UserDetailsService;

public class Provider implements Serializable {

  private static final long serialVersionUID = -8857801056358688952L;

  public static final String MULTIPLE_AUTH_PROVIDER = "multiple-authentication-provider";

  private static final Log logger = LogFactory.getLog( Provider.class );

  private String id;
  private UserDetailsService userDetailsService;
  private IUserRoleListService userRoleListService;
  private PasswordEncoder passwordEncoder;

  public Provider() {
  }

  public Provider( String id ) {
    this.id = id;
  }

  public Provider( String id, UserDetailsService userDetailsService, IUserRoleListService userRoleListService ) {
    this.id = id;
    this.userDetailsService = userDetailsService;
    this.userRoleListService = userRoleListService;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
  }

  public UserDetailsService getUserDetailsService() {
    return userDetailsService;
  }

  public void setUserDetailsService( UserDetailsService userDetailsService ) {
    this.userDetailsService = userDetailsService;
  }

  public IUserRoleListService getUserRoleListService() {
    return userRoleListService;
  }

  public void setUserRoleListService( IUserRoleListService userRoleListService ) {
    this.userRoleListService = userRoleListService;
  }

  public PasswordEncoder getPasswordEncoder() {
    return passwordEncoder;
  }

  public void setPasswordEncoder( PasswordEncoder passwordEncoder ) {
    this.passwordEncoder = passwordEncoder;
  }

  public boolean isValid() {

    boolean valid = true;

    if ( StringUtils.isEmpty( id ) ) {
      logger.warn( "invalid provider id" );
      valid = false;
    } else if ( userDetailsService == null ) {
      logger.warn( id + ": invalid userDetailsService" );
      valid = false;
    } else if ( userRoleListService == null ) {
      logger.warn( id + ": invalid userRoleListService" );
      valid = false;
    }

    return valid;
  }

  /**
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object other ) {
    if ( other != null && other instanceof Provider ) {
      return this.id.equals( ( (Provider) other ).getId() );
    }
    return false;
  }

  /**
   * @see java.lang.Object#hashCode()
   */
  public int hashCode() {
    return id.hashCode() * Serializable.class.hashCode();
  }
}
