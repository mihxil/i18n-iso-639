#!/bin/bash
export JAVA_HOME=`/usr/libexec/java_home -v 17`
export JRE_HOME=`/usr/libexec/java_home -v 17`
(cd simple ; mvn -q clean ; mvn  -q -Dexec.args=nl)
(cd jackson ; mvn -q clean ; mvn -q )
(cd jackson3 ; mvn -q clean ; mvn -q)

