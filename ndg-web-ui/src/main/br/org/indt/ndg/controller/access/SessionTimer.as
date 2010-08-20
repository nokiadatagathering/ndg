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

package main.br.org.indt.ndg.controller.access {
	
	
	import flash.events.TimerEvent;
	import flash.utils.Timer;
	
	import main.br.org.indt.ndg.i18n.ConfigI18n;
	
	import mx.controls.Alert;
	

	
	public class SessionTimer {
		
		private static var instance:SessionTimer;
		
		private static var SESSION_TIME_SECONDS:int = 1800;
		private var timer:Timer = new Timer(1000, SESSION_TIME_SECONDS);
		
		private var count:int = SESSION_TIME_SECONDS;
		[Bindable] public var timerStr:String = new String(SESSION_TIME_SECONDS);
		

		public static function getInstance():SessionTimer{
			instance = (instance == null) ? new SessionTimer(new SingletonEnforcer()) : instance;
			return instance;
		}
		
		public function SessionTimer(enforcer:SingletonEnforcer){
			if (enforcer == null){
				throw new Error("");
			} else{
	            timer.addEventListener(TimerEvent.TIMER, onTick);
	            timer.addEventListener(TimerEvent.TIMER_COMPLETE, onTimerComplete);
			}
			
		    function onTick(event:TimerEvent):void {
            	count = count - 1;
            	timerStr = new String(count);
        	}
		    function onTimerComplete(event:TimerEvent):void {
            	Alert.show(ConfigI18n.getInstance().getString("sessionExpired"),
            		ConfigI18n.getInstance().getString("lblWarning"), 0x4, null, logout);
        	}
        	function logout():void{
        		SessionClass.getInstance().logout();
        	}
		}
		
		public function startTimer():void{
			timer.start();
		}

        public function resetTimer():void{
        	timer.reset();
        	count = SESSION_TIME_SECONDS;
        	timerStr = new String(count);
        	timer.start();
        }


	}
	
}class SingletonEnforcer{}
