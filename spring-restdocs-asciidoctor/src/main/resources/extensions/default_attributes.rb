require 'asciidoctor/extensions'
require 'java'

class DefaultAttributes  < Asciidoctor::Extensions::Preprocessor
  
  def process(document, reader)
  	resolver = org.springframework.restdocs.asciidoctor.SnippetsDirectoryResolver.new()
  	attributes = document.attributes
  	attributes["snippets"] = resolver.getSnippetsDirectory(attributes) unless attributes.has_key?("snippets")
  	false
  end
  
end
 