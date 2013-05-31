
dudie-maven-plugin
==================

This is a plugin with some utilities for android development.

* [![build status](https://secure.travis-ci.org/dudie/dudie-maven-plugin.png)](https://travis-ci.org/dudie/dudie-maven-plugin) on travis-ci.org (master branch)

### Usage

Dclare a `<pluginRepository/>` in your pom:

    <project>
    ...
        <pluginRepositories>
            <pluginRepository>
                <id>dudie-maven-plugin</id>
                <url>http://dudie.github.com/dudie-maven-plugin/repository/releases</url>
                <snapshots>
                    <enabled>false</enabled>
                </snapshots>
                <releases>
                    <enabled>true</enabled>
                </releases>
            </pluginRepository>
        </pluginRepositories>
    ...
    </project>

Adding `<pluginGroup/>` to your `settings.xml` is a good idea:

    <settings>
    ...
        <pluginGroups>
            <pluginGroup>fr.dudie.maven.plugin</pluginGroup>
        </pluginGroups>
    ...
    </settings>

So you can invoke the mojo execution with:

    mvn dudie:update-eclipse-classpath

instead of:

    mvn fr.dudie.maven.plugin:update-eclipse-classpath

#### Goals

* dudie:[update-eclipse-classpath](https://github.com/dudie/dudie-maven-plugin/wiki/dudie:update-eclipse-classpath)

Update eclipse `.classpath` file for an Android project initialized with the ADT plugin.

