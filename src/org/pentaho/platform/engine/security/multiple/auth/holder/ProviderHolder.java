package org.pentaho.platform.engine.security.multiple.auth.holder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProviderHolder implements Serializable {

  private static final long serialVersionUID = -5475750887521513993L;

  private static final Log logger = LogFactory.getLog( ProviderHolder.class );

  private List<Provider> providers = new ArrayList<Provider>();

  public ProviderHolder() {
  }

  public Provider getProvider( String id ) {
    return hasProvider( id ) ? providers.get( providers.indexOf( new Provider( id ) ) ) : null;
  }

  public void clearProviders() {
    providers = new ArrayList<Provider>();
  }

  public boolean hasProvider( String id ) {
    return !StringUtils.isEmpty( id ) && hasProvider( new Provider( id ) );
  }

  public boolean hasProvider( Provider provider ) {
    return providers != null && providers.contains( provider );
  }

  public void addProvider( Provider provider ) {
    if ( !hasProvider( provider ) ) {
      providers.add( provider );
    }
  }

  public List<Provider> getProviders() {
    return providers;
  }

  public void setProviders( List<Provider> providers ) {
    this.providers = providers;
  }

  public boolean hasValidProviderList() {

    if ( providers == null ) {

      logger.warn( "Provider list is null" );
      return false;

    } else {

      for ( Provider provider : providers ) {

        if ( !provider.isValid() ) {
          return false;
        }
      }
    }

    return true;
  }
}
