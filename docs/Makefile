version:=$(shell sed -n 's/^const version *= *"\(.*\)";.*/\1/p' index.js)

run: resources/i18n-iso-639-${version}.jar
		npx serve

resources/i18n-iso-639-${version}.jar:
		mvn -q dependency:get -Dartifact=org.meeuw.i18n:i18n-iso-639:${version}
		cp ~/.m2/repository/org/meeuw/i18n/i18n-iso-639/${version}/i18n-iso-639-${version}.jar resources
