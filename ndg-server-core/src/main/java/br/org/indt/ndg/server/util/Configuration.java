/*
*  Copyright (C) 2010  INdT - Instituto Nokia de Tecnologia
*
*  NDG is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either 
*  version 2.1 of the License, or (at your option) any later version.
*
*  NDG is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public 
*  License along with NDG.  If not, see <http://www.gnu.org/licenses/ 
*/

package br.org.indt.ndg.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Classe respons�vel por carregar um arquivo de resources na mem�ria.
 * para evitar problemas de cacheamento no classloader, foi usado uma
 * estrutura de java.util.Properties ao inv�s de java.util.ResourceBundle,
 * pois este �ltimo � carregado apenas uma vez pelo Classloader, tendo que
 * se reiniciar a aplica��o toda vez que o arquivo de properties � alterado.
 * @see java.lang.ClassLoader
 * @see java.util.Properties
 * @see java.util.ResourceBundle
 */
public class Configuration extends Properties {

	private static final long serialVersionUID = 1L;

	private static Hashtable<String, Properties> managers = new Hashtable<String, Properties>();

	public synchronized static Properties getProperties(String propertyFile) throws InstantiationException {
		Properties mgr = (Properties)managers.get(propertyFile);
		if (mgr == null) {
			mgr = new Configuration(propertyFile);
			managers.put(propertyFile, mgr);
		}
		return mgr;
	}
	
	public Configuration(String file) throws InstantiationException {
		super();
		
		try {
			Properties myConfig = new Properties();
			InputStream is;

			is = getClass().getClassLoader().getResourceAsStream(file);

			if (is == null){
				throw new InstantiationException("Coudn't find configuration file: "+file);
			}
	
			myConfig.load(is);
			this.putAll(myConfig);
			myConfig = null;
			is.close();
		
		} catch (IOException e3) {
			throw new InstantiationException("Coudn't read configuration file: " + file);
		}
	}
}
