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

package main.br.org.indt.ndg.controller.access.requestAccount
{
   	import main.br.org.indt.ndg.i18n.ConfigI18n;
    
    import mx.validators.ValidationResult;
    import mx.validators.Validator;

    public class PasswordValidator extends Validator
    {
        // Define Array for the return value of doValidation().
        private var results:Array;

        public function PasswordValidator()
        {
                super();
        }

        public var confirmationSource: Object;
        public var confirmationProperty: String;

        // Define the doValidation() method.
        override protected function doValidation(value:Object):Array {

            // Call base class doValidation().
            var results:Array = super.doValidation(value.password);

            if (value.password != value.confirmation) {
                        results.push(new ValidationResult(true, null, "Mismatch",
                        ConfigI18n.getInstance().getStringFile("requestAccount", "passwordsDoesntMatch")));

            }

            return results;
        }       

        /**
         *  @private
         *  Grabs the data for the confirmation password from its different sources
         *  if its there and bundles it to be processed by the doValidation routine.
         */
        override protected function getValueFromSource():Object
        {
                var value:Object = {};

                value.password = super.getValueFromSource();

                if (confirmationSource && confirmationProperty)
                {
                        value.confirmation = confirmationSource[confirmationProperty];
                }

                return  value;
        }               

    }
}
