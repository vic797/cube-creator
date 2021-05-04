/**
 * <p><strong>Cube Creator</strong> supports plugins via it&#39;s own <strong>Plugin</strong> API. This plugins are made using the  <strong>Groovy</strong> programming language. Since this programming language supports <a href="https://en.wikipedia.org/wiki/Scripting_language">scripting</a>, the plugins do not have to be compiled to a <code>jar</code> file to be executed by <strong>Cube Creator</strong>.</p>
 * <h2 id="gidelines">Gidelines</h2>
 * <p>Before you start making a plugin make sure to read this section with the common gidelines for plugins.</p>
 * <h3 id="api">API</h3>
 * <ul>
 * <li><strong>Do</strong> use the provided API in the <code>io.github.cubecreator.plugin</code> package.</li>
 * <li><strong>Don&#39;t</strong> try to access <strong>Cube Creator</strong>&#39;s internal API. Security measures will be added in future versions to avoid access to this API.</li>
 * </ul>
 * <h3 id="files">Files</h3>
 * <ul>
 * <li><strong>Do</strong> use the plugin&#39;s <code>data</code> directory to store files and use the <strong>PluginFileSystem</strong> API.</li>
 * <li><strong>Don&#39;t</strong> try to access other plugins&#39; files or <strong>Cube Creator</strong>&#39;s own files.</li>
 * </ul>
 * <h3 id="communication">Communication</h3>
 * <ul>
 * <li><strong>Do</strong> use the <strong>{@link PluginMessage}</strong> API and the <strong>{@link PluginRequest}</strong> API to communicate with other plugins and <strong>Cube Creator</strong>.</li>
 * <li><strong>Don&#39;t</strong> try to load a class or invoke a method on other plugins or use <a href="https://www.oracle.com/technical-resources/articles/java/javareflection.html">Reflecfion</a> to access <strong>Cube Creator</strong> inner workings.</li>
 * </ul>
 * <h3 id="settings">Settings</h3>
 * <ul>
 * <li><strong>Do</strong> use the <strong>{@link PluginSettings}</strong> API to store your plugin&#39;s settings.</li>
 * <li><strong>Don&#39;t</strong> store your plugin&#39;s settings within <strong>Cube Creator</strong>&#39;s settings or other plugins&#39; settings.</li>
 * </ul>
 * <h3 id="security">Security</h3>
 * <ul>
 * <li><strong>Do</strong> use safety measures for sensitive data.</li>
 * <li><strong>Don&#39;t</strong> store or share sensitive data.</li>
 * </ul>
 * <h3 id="libraries">Libraries</h3>
 * <ul>
 * <li><strong>Do</strong> limit your plugin to the <strong>{@link Plugin}</strong> API and the <strong>Java</strong> and <strong>Swing</strong> standard libraries.</li>
 * <li><strong>Don&#39;t</strong> try to load any <code>jar</code> file.</li>
 * </ul>
 * <h3 id="usage">Usage</h3>
 * <ul>
 * <li><strong>Do</strong> use the <strong>{@link Plugin}</strong> API for it&#39;s intended purposes.</li>
 * <li><strong>Don&#39;t</strong> use the <strong>{@link Plugin}</strong> API for other things not related to <strong>Minecraft</strong> or <strong>Cube Creator</strong></li>
 * </ul>
 */
package io.github.cubecreator.plugin;