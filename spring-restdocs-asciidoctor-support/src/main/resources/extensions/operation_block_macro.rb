require 'asciidoctor/extensions'
require 'stringio'
require 'asciidoctor/logging'

# Spring REST Docs block macro to import multiple snippet of an operation at
# once
#
# Usage
#
#   operation::operation-name[snippets='snippet-name1,snippet-name2']
#
class OperationBlockMacro < Asciidoctor::Extensions::BlockMacroProcessor
  use_dsl
  named :operation
  include Asciidoctor::Logging

  def process(parent, operation, attributes)
    snippets_dir = parent.document.attributes['snippets'].to_s
    snippet_names = attributes.fetch 'snippets', ''
    operation = parent.sub_attributes operation
    snippet_titles = SnippetTitles.new parent.document.attributes
    content = read_snippets(snippets_dir, snippet_names, parent, operation,
                            snippet_titles)
    add_blocks(content, parent.document, parent) unless content.empty?
    nil
  end

  def read_snippets(snippets_dir, snippet_names, parent, operation,
                    snippet_titles)
    snippets = snippets_to_include(snippet_names, snippets_dir, operation)
    if snippets.empty?
      location = parent.document.reader.cursor_at_mark
      logger.warn message_with_context "No snippets were found for operation #{operation} in "\
           "#{snippets_dir}", source_location: location
      "No snippets found for operation::#{operation}"
    else
      do_read_snippets(snippets, parent, operation, snippet_titles)
    end
  end

  def do_read_snippets(snippets, parent, operation, snippet_titles)
    content = StringIO.new
    content.set_encoding "UTF-8"
    section_id = parent.id
    snippets.each do |snippet|
      append_snippet_block(content, snippet, section_id,
                           operation, snippet_titles, parent)
    end
    content.string
  end

  def add_blocks(content, doc, parent)
    options = { safe: doc.options[:safe], attributes: doc.attributes.clone }
    options[:attributes].delete 'leveloffset'
    fragment = Asciidoctor.load content, options
    # use a template to get the correct sectname and level for blocks to append
    template = create_section(parent, '', {})
    fragment.blocks.each do |b|
      b.parent = parent
      # might be a standard block and no section in case of 'No snippets were found for operation'
      if b.respond_to?(:sectname)
        b.sectname = template.sectname
      end
      b.level = template.level
      parent << b
    end
    parent.find_by.each do |b|
      b.parent = b.parent unless b.is_a? Asciidoctor::Document
    end
  end

  def snippets_to_include(snippet_names, snippets_dir, operation)
    if snippet_names.empty?
      all_snippets snippets_dir, operation
    else
      snippet_names.split(',').map do |name|
        path = File.join snippets_dir, operation, "#{name}.adoc"
        Snippet.new path, name
      end
    end
  end

  def all_snippets(snippets_dir, operation)
    operation_dir = File.join snippets_dir, operation
    return [] unless Dir.exist? operation_dir
    Dir.entries(operation_dir)
       .sort
       .select { |file| file.end_with? '.adoc' }
       .map { |file| Snippet.new(File.join(operation_dir, file), file[0..-6]) }
  end

  def append_snippet_block(content, snippet, section_id,
                           operation, snippet_titles, parent)
    write_title content, snippet, section_id, snippet_titles
    write_content content, snippet, operation, parent
  end

  def write_content(content, snippet, operation, parent)
    if File.file? snippet.path
      content.puts File.readlines(snippet.path, :encoding => 'UTF-8').join
    else
      location = parent.document.reader.cursor_at_mark
      logger.warn message_with_context "Snippet #{snippet.name} not found at #{snippet.path} for"\
           " operation #{operation}", source_location: location
      content.puts "Snippet #{snippet.name} not found for"\
                   " operation::#{operation}"
      content.puts ''
    end
  end

  def write_title(content, snippet, id, snippet_titles)
    section_level = '=='
    title = snippet_titles.title_for_snippet snippet
    content.puts "[[#{id}_#{snippet.name.sub '-', '_'}]]"
    content.puts "#{section_level} #{title}"
    content.puts ''
  end

  # Details of a snippet to be rendered
  class Snippet
    attr_reader :name, :path

    def initialize(path, name)
      @path = path
      @name = name
      @snippet_titles
    end
  end

  class SnippetTitles
    @defaults = { 'http-request' => 'HTTP request',
                  'curl-request' => 'Curl request',
                  'httpie-request' => 'HTTPie request',
                  'request-body' => 'Request body',
                  'request-fields' => 'Request fields',
                  'http-response' => 'HTTP response',
                  'response-body' => 'Response body',
                  'response-fields' => 'Response fields',
                  'links' => 'Links' }

    class << self
      attr_reader :defaults
    end

    def initialize(document_attributes)
      @document_attributes = document_attributes
    end

    def title_for_snippet(snippet)
      attribute_name = "operation-#{snippet.name}-title"
      @document_attributes.fetch attribute_name do
        SnippetTitles.defaults.fetch snippet.name, snippet.name.sub('-', ' ').capitalize
      end
    end
  end
end
