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

package main.br.org.indt.ndg.controller.editor
{
	public class ChecksumMD5
	{
		/*
		 * Convert a 32-bit number to a hex string with ls-byte first
		 */
		private var hex_chr:String = "0123456789abcdef";
		
		/* 
		 * somehow the expression (bitAND(b, c) | bitAND((~b), d)) didn't return coorect results on Mac
		 * for: 
		 * b&c = a8a20450, ((~b)&d) = 0101c88b, (bitAND(b, c) | bitAND((~b), d)) = a8a20450 <-- !!!
		 * looks like the OR is not executed at all.
		 *
		 * let's try to trick the P-code compiler into working with us... Prayer beads are GO!
		 */ 
		private function bitOR(a:int, b:int):int
		{   
		  var lsb:int = (a & 0x1) | (b & 0x1);
		  var msb31:int = (a >>> 1) | (b >>> 1);
		  return (msb31 << 1) | lsb;
		}
		
		
		/*  
		 * will bitXOR be the only one working...?
		 * Nope. XOR fails too if values with bit31 set are XORed. 
		
		 * Note however that OR (and AND and XOR?!) works alright for the statement
		 *  (msb31 << 1) | lsb
		 * even if the result of the left-shift operation has bit 31 set.
		 * So there might be an extra condition here (Guessmode turned on):
		 * Mac Flash fails (OR, AND and XOR) if either one of the input operands has bit31 set
		 * *and* both operands have one or more bits both set to 1. In other words: when both
		 * input bit-patterns 'overlap'.
		 * Stuff to munch on for the MM guys, I guess...
		 */
		private function bitXOR(a:int, b:int):int
		{   
		  var lsb:int = (a & 0x1) ^ (b & 0x1);
		  var msb31:int = (a >>> 1) ^ (b >>> 1);
		  return (msb31 << 1) | lsb;
		}
		  
		
		  
		/* 
		 * bitwise AND for 32-bit integers. This uses 31 + 1-bit operations internally
		 * to work around bug in some AS interpreters. (Mac Flash!)
		 */ 
		private function bitAND(a:int, b:int):int
		{   
		  var lsb:int = (a & 0x1) & (b & 0x1);
		  var msb31:int = (a >>> 1) & (b >>> 1);
		  return (msb31 << 1) | lsb;
		}
		
		/* 
		 * Add integers, wrapping at 2^32. This uses 16-bit operations internally
		 * to work around bugs in some AS interpreters. (Mac Flash!)
		 */ 
		private function addme(x:int, y:int):int 
		{
		  var lsw:int = (x & 0xFFFF)+(y & 0xFFFF);
		  var msw:int = (x >> 16)+(y >> 16)+(lsw >> 16);
		  return (msw << 16) | (lsw & 0xFFFF);
		}
		
		private function rhex(num:int):String
		{
		  var str:String = "";
		  for(var j:int = 0; j <= 3; j++)
		  {
		    str += hex_chr.charAt((num >> (j * 8 + 4)) & 0x0F) +
		           hex_chr.charAt((num >> (j * 8)) & 0x0F);
		  }
		  return str;
		}
		
		/*
		 * Convert a string to a sequence of 16-word blocks, stored as an array.
		 * Append padding bits and the length, as described in the MD5 standard.
		 */
		private function str2blks_MD5(str:String):Array
		{
		  var nblk:int = ((str.length + 8) >> 6) + 1;  // 1 + (len + 8)/64
		  var blks:Array = new Array(nblk * 16);
		  for(var i:int = 0; i < nblk * 16; i++) blks[i] = 0;
				
		  for(i = 0; i < str.length; i++)
		  {
		    blks[i >> 2] |= str.charCodeAt(i) << (((str.length * 8 + i) % 4) * 8);
		  }
		  blks[i >> 2] |= 0x80 << (((str.length * 8 + i) % 4) * 8);
		  var l:int = str.length * 8;
		  blks[nblk * 16 - 2] = (l & 0xFF);
		  blks[nblk * 16 - 2] |= ((l >>> 8) & 0xFF) << 8;
		  blks[nblk * 16 - 2] |= ((l >>> 16) & 0xFF) << 16;
		  blks[nblk * 16 - 2] |= ((l >>> 24) & 0xFF) << 24;
		
		  return blks;
		}
		
		/*
		 * Bitwise rotate a 32-bit number to the left
		 */
		private function rol(num:int, cnt:int):int
		{
		  return (num << cnt) | (num >>> (32 - cnt));
		}
		
		/*
		 * These functions implement the basic operation for each round of the
		 * algorithm.
		 */
		private function cmn(q:int, a:int, b:int, x:int, s:int, t:int):int
		{
		  return addme(rol((addme(addme(a, q), addme(x, t))), s), b);
		}
		private function ff(a:int, b:int, c:int, d:int, x:int, s:int, t:int):int
		{
		  return cmn(bitOR(bitAND(b, c), bitAND((~b), d)), a, b, x, s, t);
		}
		private function gg(a:int, b:int, c:int, d:int, x:int, s:int, t:int):int
		{
		  return cmn(bitOR(bitAND(b, d), bitAND(c, (~d))), a, b, x, s, t);
		}
		private function hh(a:int, b:int, c:int, d:int, x:int, s:int, t:int):int
		{
		  return cmn(bitXOR(bitXOR(b, c), d), a, b, x, s, t);
		}
		private function ii(a:int, b:int, c:int, d:int, x:int, s:int, t:int):int
		{
		  return cmn(bitXOR(c, bitOR(b, (~d))), a, b, x, s, t);
		}
		
		/*
		 * Take a string and return the hex representation of its MD5.
		 */
		public function calcMD5(str:String):String
		{
			
			var a:int,b:int,c:int,d:int,olda:int,oldb:int,oldc:int,oldd:int;
			var x:Array;
			
		  x = str2blks_MD5(str);
		  a =  1732584193;
		  b = -271733879;
		  c = -1732584194;
		  d =  271733878;
		  var step:int;
		
		  for(var i:int = 0; i < x.length; i += 16)
		  {
		    olda = a;
		    oldb = b;
		    oldc = c;
		    oldd = d;
		    
		    step = 0;
		    a = ff(a, b, c, d, x[i+ 0], 7 , -680876936);
		    d = ff(d, a, b, c, x[i+ 1], 12, -389564586);
		    c = ff(c, d, a, b, x[i+ 2], 17,  606105819);
		    b = ff(b, c, d, a, x[i+ 3], 22, -1044525330);
		    a = ff(a, b, c, d, x[i+ 4], 7 , -176418897);
		    d = ff(d, a, b, c, x[i+ 5], 12,  1200080426);
		    c = ff(c, d, a, b, x[i+ 6], 17, -1473231341);
		    b = ff(b, c, d, a, x[i+ 7], 22, -45705983);
		    a = ff(a, b, c, d, x[i+ 8], 7 ,  1770035416);
		    d = ff(d, a, b, c, x[i+ 9], 12, -1958414417);
		    c = ff(c, d, a, b, x[i+10], 17, -42063);
		    b = ff(b, c, d, a, x[i+11], 22, -1990404162);
		    a = ff(a, b, c, d, x[i+12], 7 ,  1804603682);
		    d = ff(d, a, b, c, x[i+13], 12, -40341101);
		    c = ff(c, d, a, b, x[i+14], 17, -1502002290);
		    b = ff(b, c, d, a, x[i+15], 22,  1236535329);    
		    a = gg(a, b, c, d, x[i+ 1], 5 , -165796510);
		    d = gg(d, a, b, c, x[i+ 6], 9 , -1069501632);
		    c = gg(c, d, a, b, x[i+11], 14,  643717713);
		    b = gg(b, c, d, a, x[i+ 0], 20, -373897302);
		    a = gg(a, b, c, d, x[i+ 5], 5 , -701558691);
		    d = gg(d, a, b, c, x[i+10], 9 ,  38016083);
		    c = gg(c, d, a, b, x[i+15], 14, -660478335);
		    b = gg(b, c, d, a, x[i+ 4], 20, -405537848);
		    a = gg(a, b, c, d, x[i+ 9], 5 ,  568446438);
		    d = gg(d, a, b, c, x[i+14], 9 , -1019803690);
		    c = gg(c, d, a, b, x[i+ 3], 14, -187363961);
		    b = gg(b, c, d, a, x[i+ 8], 20,  1163531501);
		    a = gg(a, b, c, d, x[i+13], 5 , -1444681467);
		    d = gg(d, a, b, c, x[i+ 2], 9 , -51403784);
		    c = gg(c, d, a, b, x[i+ 7], 14,  1735328473);
		    b = gg(b, c, d, a, x[i+12], 20, -1926607734);
		    a = hh(a, b, c, d, x[i+ 5], 4 , -378558);
		    d = hh(d, a, b, c, x[i+ 8], 11, -2022574463);
		    c = hh(c, d, a, b, x[i+11], 16,  1839030562);
		    b = hh(b, c, d, a, x[i+14], 23, -35309556);
		    a = hh(a, b, c, d, x[i+ 1], 4 , -1530992060);
		    d = hh(d, a, b, c, x[i+ 4], 11,  1272893353);
		    c = hh(c, d, a, b, x[i+ 7], 16, -155497632);
		    b = hh(b, c, d, a, x[i+10], 23, -1094730640);
		    a = hh(a, b, c, d, x[i+13], 4 ,  681279174);
		    d = hh(d, a, b, c, x[i+ 0], 11, -358537222);
		    c = hh(c, d, a, b, x[i+ 3], 16, -722521979);
		    b = hh(b, c, d, a, x[i+ 6], 23,  76029189);
		    a = hh(a, b, c, d, x[i+ 9], 4 , -640364487);
		    d = hh(d, a, b, c, x[i+12], 11, -421815835);
		    c = hh(c, d, a, b, x[i+15], 16,  530742520);
		    b = hh(b, c, d, a, x[i+ 2], 23, -995338651);
		    a = ii(a, b, c, d, x[i+ 0], 6 , -198630844);
		    d = ii(d, a, b, c, x[i+ 7], 10,  1126891415);
		    c = ii(c, d, a, b, x[i+14], 15, -1416354905);
		    b = ii(b, c, d, a, x[i+ 5], 21, -57434055);
		    a = ii(a, b, c, d, x[i+12], 6 ,  1700485571);
		    d = ii(d, a, b, c, x[i+ 3], 10, -1894986606);
		    c = ii(c, d, a, b, x[i+10], 15, -1051523);
		    b = ii(b, c, d, a, x[i+ 1], 21, -2054922799);
		    a = ii(a, b, c, d, x[i+ 8], 6 ,  1873313359);
		    d = ii(d, a, b, c, x[i+15], 10, -30611744);
		    c = ii(c, d, a, b, x[i+ 6], 15, -1560198380);
		    b = ii(b, c, d, a, x[i+13], 21,  1309151649);
		    a = ii(a, b, c, d, x[i+ 4], 6 , -145523070);
		    d = ii(d, a, b, c, x[i+11], 10, -1120210379);
		    c = ii(c, d, a, b, x[i+ 2], 15,  718787259);
		    b = ii(b, c, d, a, x[i+ 9], 21, -343485551);
		
		    a = addme(a, olda);
		    b = addme(b, oldb);
		    c = addme(c, oldc);
		    d = addme(d, oldd);
		  }
		  return rhex(a) + rhex(b) + rhex(c) + rhex(d);
		}
 	}
}
