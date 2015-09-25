/**
 * <p>This is the main Binson package; most users will not need to 
 * use any other package.
 * Binson is an exceptionally simple binary data serialization format. 
 * It is similar in scope to JSON, but is faster, more compact, and simpler.</p>
 * 
 * <p>This Binson implementation has support for generating and parsing JSON which
 * makes conversions between JSON and Binson very easy.</p>
 * 
 * <h2>Code</h2>
 * 
 * <p>The code below first creates a Binson object with two fields: 
 * one int named 'a' and one string named 's'.
 * Then 'obj' is serialized to bytes.
 * The third line parses back the bytes to a BinsonObject again, and
 * the last line checks that the parsed Binson object is the same as the original one.</p>
 * 
 * <pre>  Binson obj = new Binson().put(&quot;a&quot;, 123).put(&quot;s&quot;, &quot;Hello world!&quot;);
 *  byte[] bytes = obj.toBytes();
 *  Binson obj2 = Binson.fromBytes(bytes);
 *  assert obj2.equals(obj);
 * </pre>
 * 
 * <p>Instead of using byte arrays, a Binson object can serialized to an OutputStream 
 * and parsed from an InputStream.</p>
 * 
 * <p>This package contains JSON (http://json.org/) support. See methods 
 * <code>fromJson()</code> and <code>toJson()</code> in <code>Binson</code>.</p>
 * 
 * @author Frans Lundberg
 */
package org.binson;
