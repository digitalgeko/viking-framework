#!/usr/bin/env groovy

def docsFile = new File("README.md")

def titles = []

docsFile.eachLine {
	if (it.startsWith("#")) {
		def titleLevel = it.substring(0, it.indexOf(" ")).length()
		if (titleLevel <= 2) {
			def title = it.substring(it.indexOf(" ")+1)
			def spacing = ( " " * (titleLevel-1) )

			println spacing + "* [$title](#${title.toLowerCase().replace(" ", "-")})"	
		}
	}
}
println ""