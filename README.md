EmberJS IntelliJ plugin
=======================

__WARNING:__ This project is obsolete. Checkout the new [intellij-emberjs](https://github.com/Turbo87/intellij-emberjs) project instead which implemented a lot of the same ideas and is based on the `ember-cli` conventions!

EmberJS Support for the Intellij Platform (WebStorm, PhpStorm, Rubymine, Intellij, etc).

This is still very much a Work In Progress. Please help make this a great plugin!!!

Status
------

Currently WIP and experimental. You are welcome to take this project an"as is" and continue development in any direction you like. Keep me posted ;)

Leveraging the AngularJS plugin infrastructure
----------------------------------------------

Since 0.1, the plugin has been extended to leverage much of the angular plugin infrastructure and there is a surprising amount of infrastructure that can be used for similar EmberJS functionality.

Other repos for inspiration (ie "stealing" code)

https://bitbucket.org/sylvanaar2/lua-for-idea/src

Requires Atlassian SourceTree to clone it:

http://www.sourcetreeapp.com/download/ https://confluence.atlassian.com/pages/viewpage.action?pageId=269981802 https://confluence.atlassian.com/display/BBKB/abort%3A+certificate+for+bitbucket.org+has+unexpected+fingerprint

Handlebars and Mustache https://github.com/dmarcotte/idea-handlebars

Current structure:

Almost everything is under the `org.emberjs` package namespace.

### New EmberJS Project

`project` will contain The New Project action that will run Ember CLI under the covers in order to create the project. Also see `NewEmberJSProject`. Not yet sure how to set this up correctly, so please help out.

[IntelliJ API](http://grepcode.com/snapshot/repository.grepcode.com/java/ext/com.jetbrains/intellij-idea/13.0.0/)

### Navigation

`EmberGotoSymbolContributor` goes to the file for a given Ember Symbol.

`EmberJSIndexingHandler` will find and index EmberJS entities such as Component, Controller, Model etc. Each such entity has their own Index class such as `EmberComponentIndex`.

`EmberJSTemplatesProvider` defines which Live Templates are made available. Each such template can be found under the `resources` src module, and each template code snippet defined under which contexts it is available. There are templates defined for various scripting languages.

`EmberJSDocumentationProvider` is meant to look up Documentation for an Ember class using the emberjs main documentation site.

`EmberItem` is a utility class, mapped directly from `AngularItem` of the angular plugin.

`EmberTemplateCacheIndex` is mapped directly from `AngularTemplateCacheIndex`. It is meant to find and cache any Ember handlebars templates defined inline in HTML as `type="text/x-handlebars"`.

`settings` is a package that contains Ember project configuration settings. We should here learn and reuse more from the Lua plugin.

`EmberJSComponentRenameProcessor` is currently the only refactoring supported. It is meant to enable changing a component name and then find all usages in templates and rename accordingly using Ember naming conventions (template component name is in lowercase with dashes).

`lang` package contains Ember language definitions. Here the main point of interest is the `lang.psi` package which will contain the main Ember "constructs" that the plugin should recognize as "language equivalents" and treat in some special ways (for intentions, refactorings etc). Examples are: `Action` and various kinds of `Property`.

`intentions` package currently contains duplicate logic of `EmberJSDocumentationProvider`. We should instead provide more meaningful intentions. See Lua and other plugins for ideas/inspiration.

`findUsages` package contains `EmberJSReferenceSearcher` which can be used to find indexed ember js references (I think?). Stolen from angulajs plugin...

`editor` package is currently empty. Fill it up as needed ;)

`codeInsight` is an important package for code insight help, such as completion suggestions etc. Would be really nice if it could help out with properties, warning you if you attempt to get/set properties that are not defined in the current "scope".

It should/could perhaps also contain clever logic for handlebars, linking to components definition and a nice way to use handlebars `each` similar to `ng-repeat` for angular.

Current features (0.1)
----------------------

-	Navigate to Ember code
-	Live Templates (js, ls, coffee)
-	JS Docs lookup

### Navigate to Ember code

This code/logic is currently found in `GotoEmberAction.java`

via `ctrl l`

`<some-variable> = Ember.Route.create({`

Looking for `posts` will match on:

`PostsRoute = Ember.Route.create({`

Using match rule on first group `$1`, ie. `(Posts)`

`(Posts)Route = Ember.Route.create({`

And so on for:

-	Component
-	Controller
-	Route
-	Model
-	View
-	Mixin

*Tests*

`moduleFor('<ember-artifact-type>:<name>',`

Looking for `post` will match on:

`moduleFor('controller:post',`

`moduleForModel('post',`

And when looking for `bling`

`moduleForComponent('bling-it',`

*Limitations*

-	Currently only supports single quotes `'`
-	No distinction between real artifacts (Model) and moduleFor used for Test.

See `TODO` comments in the code for `GotoEmberAction.java` and help improve it :)

### Live Templates

-	javascript
-	coffeescript
-	emberscript

This is configured via `EmberJSTemplatesProvider.java`

```java
@Override
public String[] getDefaultLiveTemplateFiles() {
    return new String[] {
        "liveTemplates/EmberJS",
        "liveTemplates/EmberJS-CoffeeScript",
        "liveTemplates/EmberJS-EmberScript"
    };
}
```

For `liveTemplates/EmberJS-CoffeeScript`

```xml
  <option name="COFFEE_SCRIPT" value="true" />
  <option name="LIVE_SCRIPT" value="true" />
```

*Routing*

-	"emre": resource route
-	"emrep": resource route with params
-	"emro": route

*General*

-	"emsf": store find
-	"emac": actions scope
-	"empr": property
-	"emtr": transitionTo
-	".g": get property
-	".s": set property
-	"emca": computed alias

*DataStore - Ember Data*

-	"dsa": DS attribute
-	"dsbt": DS belongsTo
-	"dshm": DS hasMany

*Ember classes*

-	"emoc": Ember.Object.create
-	"emap": Ember.ArrayProxy.create
-	"emma": Ember.MutableArray.create
-	"emen": Ember.Enumerable.create
-	"emns": Ember.Namespace.create

*Emberscript specific templates*

-	"com": Computed property
-	"obs": Observed property
-	"vol": Volatile property
-	"emoc": Object Create (via `class`\)
-	"emmi": Mixin Create (via `mixin`\)

To use [emberscript](http://emberscript.com/), you need to configure your IDE to use the @kristianmandrup ES6 compatible fork of [emberscript](https://github.com/kristianmandrup/ember-script), which enables*multi-script* compilation (multiple languages in the same file!!)

This resolves issue [#44](https://github.com/ghempton/ember-script/issues/44)

### JS Docs intention

See `OpenEmberJSDocsIntention.java`

Will get the classname at the caret position and lookup JS documentation from Ember API:

```java
StringBuilder name = new StringBuilder("http://emberjs.com/api/classes/Ember." + clazz);
BrowserUtil.open(name.toString());
```

### Planned features

-	New Project creator using *Ember-CLI*
-	Browse Ember CLI addons/libraries per category (listed per rating) and install auto-magically!!

+Code insights+

-	referencing unknown module via `moduleFor`
-	Warning if `this.get` or `this.set` references undeclared property
-	Understanding `.extend` and `Mixins` are part of class hierarchy (class scope)

+Project configuration+ - Ignore folders: `bower_components`, `node_modules`, `tmp`, `dist`

Please add your own suggestions ;)

Contributing
------------

See [plugin-in-30-minutes](http://bjorn.tipling.com/how-to-make-an-intellij-idea-plugin-in-30-minutes)

### Step 0: Download and install Ultimate Edition (trial 30 days) of IntelliJ IDEA

https://www.jetbrains.com/idea/download

Sadly only the Ultimate Edition has the `javascript.jar` plugin which is needed as a building block for any javascript plugin development!

[com.intellij.lang.javascript](http://devnet.jetbrains.com/message/5526006#5526006)

### Step 1: Download and install Community Edition of IntelliJ IDEA

*download and install* the *Community version of IntelliJ IDEA* if you don’t already have it.

This is perhaps (still?) useful after your Ultimate edition trial expires?

### Step 2: Do a shallow git clone of the Community Edition source code

`git clone --depth 1 https://github.com/JetBrains/intellij-community.git`

### Step 3: Create a project and set up your project structure.

-	Fire up IntelliJ Ultimate Edition.
-	Create project from existing source
-	Get a Java JDK. Java 1.6 recommended.
-	Configure the Javas SDK via Project Structure

Once you have downloaded a Java JDK, you’ll need to create an SDK for this in IntelliJ IDEA In the project structure dialog that appears after you create a new project, create a new SDK for the version of Java you downloaded.

Setup a Plugin SDK by clicking the *“new” button* next to the Project SDK.

It will prompt you to locate the IntelliJ that you installed.

On a Mac the path for this is:

`/Applications/IntelliJ IDEA 13.app`

It should display the `Content` folder, just click "next".

On Windows the path is something like:

`C:\\Program Files (x86)\JetBrains\IntelliJ IDEA Ultimate Edition 13.0.1`

Finally you need to add any extra plugin jars that are required (and used) for this plugin.

In this case we need support for JavaScriptLanguage features, so we need the `JavaScriptLanguage.jar` in the class path. Possibly also `javascript-openapi` and `ecmascript6-compiler`.

On Mac OSX:

`/Applications/IntelliJ IDEA 13.app/Contents/plugins/JavaScriptLanguage/lib/JavaScriptLanguage.jar`

Now if the project is configured correctly, the IDE should code inspection and highlight any errors in red etc.

Create a new *Run configuration*, using "+", and select "Plugin", then click "next".

Now run "Debug EmberJS". Create or select a dummy ember-cli project and test the plugin features there :)

*Reference plugin*

https://github.com/JetBrains/intellij-plugins/tree/master/AngularJS

The *AngularJS plugin* will be our main reference, as it contains the most features required by a full JS multilayered MV* framework like *EmberJS*.

IF you want, you can try doing a [sparse-checkout](http://jasonkarns.com/blog/subdirectory-checkouts-with-git-sparse-checkout/) of only this plugin directory...

### Development links

Some useful links to get a better understanding of Plugin development for the IDEA platform

Developing a Plugin for IntelliJ IDEA - Some Useful Tips and Links http://tomaszdziurko.pl/2011/09/developing-plugin-intellij-idea-some-tips-and-links/

How to make an IntelliJ IDEA plugin in less than 30 minutes http://bjorn.tipling.com/how-to-make-an-intellij-idea-plugin-in-30-minutes

IntelliJ IDEA Plugin development Tutorial videos https://www.youtube.com/watch?v=S1u8uVjPjvc&list=PLQ176FUIyIUYi8c0HCxlV8c9cINmBbBky

Live Coding an IntelliJ IDEA Plugin from Scratch https://www.youtube.com/watch?v=-ZmQD6Fr6KE

Webinar Recording: How to Build Developer Tools on Top of IntelliJ Platform https://www.youtube.com/watch?v=S1u8uVjPjvc&list=PLQ176FUIyIUYi8c0HCxlV8c9cINmBbBky&index=3

AngularJS plugin (which is main inspiration for this plugin) https://github.com/denofevil/angularjs-plugin

Most Recent (and complete) AngularJS plugin https://github.com/JetBrains/intellij-plugins/tree/master/AngularJS

IntelliJ IDEA API http://grepcode.com/snapshot/repository.grepcode.com/java/ext/com.jetbrains/intellij-idea/13.0.0/

Forum: Open API and Plugin Development http://devnet.jetbrains.com/community/idea/open_api_and_plugin_development

Check Out More Than 200 Open-Source Plugins http://blog.jetbrains.com/idea/2012/10/check-out-more-than-200-open-source-plugins/

Full list of +200 https://docs.google.com/spreadsheet/lv?key=0ApDPbpSNCArIdE9nWF9CRjgxUHBHZV8xeDNKWUZ0Zmc&rm=full#gid=0

EditorUtil https://github.com/JetBrains/intellij-community/blob/master/platform/platform-impl/src/com/intellij/openapi/editor/ex/util/EditorUtil.java

PsiUtil https://github.com/JetBrains/intellij-community/blob/master/java/java-psi-api/src/com/intellij/psi/util/PsiUtil.java

IntelliJ IDEA Plugin Structure http://confluence.jetbrains.com/display/IDEADEV/IntelliJ+IDEA+Plugin+Structure

Plugin Development http://confluence.jetbrains.com/display/IDEADEV/PluginDevelopment

Plugin Development FAQ http://confluence.jetbrains.com/display/IDEADEV/Plugin+Development+FAQ

Writing Plugins http://www.jetbrains.org/display/IJOS/Writing+Plug-ins

Live Templates http://blog.jetbrains.com/webide/2012/10/high-speed-coding-with-custom-live-templates/ https://www.jetbrains.com/idea/webhelp/creating-code-constructs-by-live-templates.html

Developing Custom Language Plugins for IntelliJ IDEA https://confluence.jetbrains.com/display/IDEADEV/Developing+Custom+Language+Plugins+for+IntelliJ+IDEA

Code assistance https://www.jetbrains.com/idea/features/code_assistance.html

Existing Javascript plugins for inspiration
-------------------------------------------

[List of Javascript plugins](https://plugins.jetbrains.com/category/index?pr=&category_id=50)

Some candidates to look at:

-	[CommonJSAutoComplete](https://github.com/jballant/CommonJSAutoComplete)
-	[jsToolbox](https://github.com/andresdominguez/jsToolbox)
-	[JSIntentionPowerPack](https://plugins.jetbrains.com/plugin/264?pr=)
-	[ScaffAngular](https://github.com/suparngp/angular-iunit)
-	...

Also clone and look at these [intellij-plugins](https://github.com/JetBrains/intellij-plugins)

Installing the EmberJS plugin for Webstorm
------------------------------------------

Ideally we would like this plugin to be directly installable from Webstorm via the plugin repository. However so far, this is a plugin under development and needs some testing before we release it into the wild. So please help out.

It looks like you can manually install a plugin on Mac OSX like this:

-	Create a jar file from this repo. http://stackoverflow.com/questions/19650711/how-to-create-a-java-jar-file-with-source-code-java-files
-	Create a jar with IntelliJ
-	http://blog.jetbrains.com/idea/2010/08/quickly-create-jar-artifact/

-	Go to your *Applications* folder

-	Select *View package content* from context menu (Command-click)

-	Browse to `Content/plugins`

-	Create a folder `EmberJS`

-	in that folder create a `lib` folder

-	add a `EmberJS.jar` to the `lib` folder

### Some time in the not too distant future...

As soon as we have confirmed that this first version of the plugin is functional ;)

-	Navigate to File->Settings->Plugins
-	Click "Browse Repositories"
-	Select "EmberJS"
-	Double-click (or right-click) and, when prompted, choose "Yes"
-	Restart WebStorm
