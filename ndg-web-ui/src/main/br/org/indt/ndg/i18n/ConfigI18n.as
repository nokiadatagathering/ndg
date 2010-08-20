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

package main.br.org.indt.ndg.i18n {
	
	import mx.resources.ResourceManager;
	
	
	
	
	[ResourceBundle("locale")]
	[ResourceBundle("requestAccount")]
	[Bindable]
	public class ConfigI18n {
	
		private static var instance:ConfigI18n;
	
		public static const LOCALE_FILE:String = "locale";
		public static const en_US:String = "en_US";
		public static const pt_BR:String = "pt_BR";
		public static const es_ES:String = "es_ES";
		
		public var locales:Array = [ConfigI18n.en_US, ConfigI18n.pt_BR, ConfigI18n.es_ES];
		private var currentLocale:String = "en_US";
		
		
		public function ConfigI18n(enforcer:SingletonEnforcer) {
			if (enforcer == null){
				throw new Error("");
			}
		}
		
		public static function getInstance():ConfigI18n{
			instance = (instance == null) ? new ConfigI18n(new SingletonEnforcer()) : instance;
			return instance;
		}
		
		
		public function setCurrentLocale(currentLocale:String):void {
			this.currentLocale = currentLocale;
			ResourceManager.getInstance().localeChain = [this.currentLocale];
		}
		
		public function getCurrentLocale():String{
			return this.currentLocale;
		}
		
		public function getString(key:String):String{
			return ResourceManager.getInstance().getString(ConfigI18n.LOCALE_FILE, key);
		}
		
		public function getStringFile(file:String, key:String):String{
			return ResourceManager.getInstance().getString(file, key);
		}		
		
	}
	
}
class SingletonEnforcer{}
