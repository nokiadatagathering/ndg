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

package main.br.org.indt.ndg.controller.editor
{
	import mx.controls.treeClasses.*;
	import mx.collections.ICollectionView;
	import mx.collections.IViewCursor;
	public class MyCustomTreeDataDescriptor implements ITreeDataDescriptor
	{
		public var DefaultDataDesc:DefaultDataDescriptor;
		public function MyCustomTreeDataDescriptor()
			{
				DefaultDataDesc = new DefaultDataDescriptor();   
		}
		
		public function addChildAt(parent:Object, child:Object, index:int, 
	            model:Object=null):Boolean 
	    {
	        return false;
	    }
	    
	    public function getChildren(node:Object, model:Object=null):ICollectionView
	    {
	        return DefaultDataDesc.getChildren(node, model);
	    }
	    
	    public function getData(node:Object, model:Object=null):Object 
	    {
	        try {
	            return node;
	        }
	        catch (e:Error) {
	        }
	        return null;
       	}
       	
       	public function hasChildren(node:Object, model:Object=null):Boolean 
       	{
	        if (node == null) 
	            return false;
		    var children:ICollectionView = getChildren(node, model);
	        try {
	            if (children.length > 0)
	            {
	                return true;
	            }
	        }
	        catch (e:Error) {
	        }
	        return false;
	    }
	    
	    public function isBranch(node:Object, model:Object=null):Boolean 
	    {
	        try {
	            if (node is Object) {
	            	var nodeName:String = node.localName() as String;
	            	
	            	if (nodeName == "question") {
	            		return false;
	            	}
	            	else if (nodeName == "category") {
	            		return true;	
	            	}
	            }
	        }
	        catch (e:Error) {
	            trace("[Descriptor] exception checking for isBranch");
	        }
	        return false;
    	}
    	
    	public function removeChildAt(parent:Object, child:Object, index:int, model:Object=null):Boolean
    	{
	        return false;
    	}
  
 }
    
}