/*
*  Nokia Data Gathering
*
*  Copyright (C) 2011 Nokia Corporation
*
*  This program is free software; you can redistribute it and/or
*  modify it under the terms of the GNU Lesser General Public
*  License as published by the Free Software Foundation; either
*  version 2.1 of the License, or (at your option) any later version.
*
*  This program is distributed in the hope that it will be useful,
*  but WITHOUT ANY WARRANTY; without even the implied warranty of
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
*  Lesser General Public License for more details.
*
*  You should have received a copy of the GNU Lesser General Public License
*  along with this program.  If not, see <http://www.gnu.org/licenses/
*/

package main.br.org.indt.ndg.controller.util {
	
	
	
	import flash.external.*;
	import flash.utils.*;
	
	
	public class HtmlAttribute {
		
		private var _queryString:String;
		private var _all:String;
		private var _params:Object;
		
		public function get queryString():String {
			return _queryString;
		}
		
		public function get url():String {
			return _all;
		}
		
		public function get parameters():Object {
			return _params;
		}		
		
		public function HtmlAttribute(){
			readQueryString();
		}

		private function readQueryString():void {
			_params = {};
			try{
				_all =  ExternalInterface.call("window.location.href.toString");
				_queryString = ExternalInterface.call("window.location.search.substring", 1);
				if(_queryString){
					var params:Array = _queryString.split('&');
					var length:uint = params.length;
					for (var i:uint=0,index:int=-1; i<length; i++) {
						var kvPair:String = params[i];
						if((index = kvPair.indexOf("=")) > 0){
							var key:String = kvPair.substring(0,index);
							var value:String = kvPair.substring(index+1);
							_params[key] = value;
						}
					}
				}
			} catch(e:Error){
				trace("Some error occured. ExternalInterface doesn't work in Standalone player.");
			}
		}
	}
	
	
	
}