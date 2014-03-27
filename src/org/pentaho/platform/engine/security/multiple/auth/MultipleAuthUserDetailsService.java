/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.platform.engine.security.multiple.auth;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.security.multiple.auth.holder.Provider;
import org.pentaho.platform.engine.security.multiple.auth.holder.ProviderHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class MultipleAuthUserDetailsService implements UserDetailsService {

  private static final Log logger = LogFactory.getLog( MultipleAuthUserDetailsService.class );

  private ProviderHolder holder;

  public MultipleAuthUserDetailsService() {
    super();
  }

  @Override
  public UserDetails loadUserByUsername( String username ) throws UsernameNotFoundException, DataAccessException {

    UserDetails userDetails = null;

    for ( Provider provider : getProviders() ) {

      logger.info( "Attempting to load user " + username + " via " + provider.getId() );

      try {

        if ( ( userDetails = provider.getUserDetailsService().loadUserByUsername( username ) ) != null ) {

          logger.info( "user fetched via " + provider.getId() );
          return userDetails;
        }

      } catch ( UsernameNotFoundException e ) {
        logger.info( provider.getId() + " does not know user " + username );
        // continue searching
      }
    }

    logger.error( "user " + username + " not found" );
    throw new UsernameNotFoundException( username );
  }

  public String getProviderIdForUser( String username ) {

    for ( Provider provider : getProviders() ) {

      try {

        if ( ( provider.getUserDetailsService().loadUserByUsername( username ) ) != null ) {
          return provider.getId();
        }

      } catch ( UsernameNotFoundException e ) {
        // continue searching
      }
    }
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
