/*
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
 * Copyright 2012 Pentaho Corporation.  All rights reserved.
 *
 */
package org.pentaho.platform.engine.security.userrole;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.engine.security.messages.Messages;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 *
 * @author pedro
 */
public class MultiUserRoleListService implements IUserRoleListService, InitializingBean {

    private List<IUserRoleListService> userRoleListServices;
    private Comparator<String> usernameComparator;
    private Comparator<GrantedAuthority> grantedAuthorityComparator;

    public MultiUserRoleListService() {

        super();
        userRoleListServices = new ArrayList<IUserRoleListService>();

    }

    public void afterPropertiesSet() throws Exception {

        if (this.userRoleListServices == null || this.userRoleListServices.isEmpty()) {
            throw new Exception(Messages.getString("UserDetailsRoleListService.ERROR_0001_USERROLELISTSERVICE_NOT_SET")); //$NON-NLS-1$
        }
    }

    public void setUserRoleListServices(final IUserRoleListService[] values) {

        for (int i = 0; i < values.length; i++) {
            IUserRoleListService iUserRoleListService = values[i];
            this.userRoleListServices.add(iUserRoleListService);
        }

    }

    public void setGrantedAuthorityComparator(final Comparator<GrantedAuthority> grantedAuthorityComparator) {
        Assert.notNull(grantedAuthorityComparator);
        this.grantedAuthorityComparator = grantedAuthorityComparator;
    }

    public void setUsernameComparator(final Comparator<String> usernameComparator) {
        Assert.notNull(usernameComparator);
        this.usernameComparator = usernameComparator;
    }

    public GrantedAuthority[] getAllAuthorities() {

        Set<GrantedAuthority> results = new HashSet<GrantedAuthority>();

        for (Iterator<IUserRoleListService> it = userRoleListServices.iterator(); it.hasNext();) {
            IUserRoleListService iUserRoleListService = it.next();
            results.addAll(Arrays.asList(iUserRoleListService.getAllAuthorities()));

        }

        GrantedAuthority[] ret = results.toArray(new GrantedAuthority[0]);
        if (null != grantedAuthorityComparator) {
            Arrays.sort(ret, grantedAuthorityComparator);
        } else {
            Arrays.sort(ret);
        }

        return ret;

    }

    public String[] getAllUsernames() {

        Set<String> results = new HashSet<String>();

        for (Iterator<IUserRoleListService> it = userRoleListServices.iterator(); it.hasNext();) {
            IUserRoleListService iUserRoleListService = it.next();
            results.addAll(Arrays.asList(iUserRoleListService.getAllUsernames()));
        }

        String[] ret = results.toArray(new String[0]);
        if (null != usernameComparator) {
            Arrays.sort(ret, usernameComparator);
        } else {
            Arrays.sort(ret);
        }
        return ret;

    }

    /**
     * Search for the usernames
     *
     * @param authority
     * @return
     */
    public String[] getUsernamesInRole(GrantedAuthority authority) {

        Set<String> results = new HashSet<String>();

        for (Iterator<IUserRoleListService> it = userRoleListServices.iterator(); it.hasNext();) {
            IUserRoleListService iUserRoleListService = it.next();
            results.addAll(Arrays.asList(iUserRoleListService.getUsernamesInRole(authority)));
        }

        String[] ret = results.toArray(new String[0]);
        if (null != usernameComparator) {
            Arrays.sort(ret, usernameComparator);
        } else {
            Arrays.sort(ret);
        }
        return ret;
    }

    public GrantedAuthority[] getAuthoritiesForUser(String username) {

        Set<GrantedAuthority> results = new HashSet<GrantedAuthority>();

        for (Iterator<IUserRoleListService> it = userRoleListServices.iterator(); it.hasNext();) {
            IUserRoleListService iUserRoleListService = it.next();

            try{
                results.addAll(Arrays.asList(iUserRoleListService.getAuthoritiesForUser(username)));
            }
            catch(Exception UsernameNotFoundException){
                // In multiple providers, this errors are not uncommon
            }
            
        }

        GrantedAuthority[] ret = results.toArray(new GrantedAuthority[0]);
        if (null != grantedAuthorityComparator) {
            Arrays.sort(ret, grantedAuthorityComparator);
        } else {
            Arrays.sort(ret);
        }

        return ret;

    }
}
