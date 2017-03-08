require 'asciidoctor/extensions'
require 'stringio'

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

  def process(parent, operation, attributes)
    snippets_dir = parent.document.attributes['snippets'].to_s
    snippet_names = attributes.fetch 'snippets', ''
    content = read_snippets(snippets_dir, snippet_names, parent, operation)
    add_blocks(content, parent.document, parent) unless content.empty?
    nil
  end

  def read_snippets(snippets_dir, snippet_names, parent, operation)
    snippets = snippets_to_include(snippet_names, snippets_dir, operation)
    if snippets.empty?
      warn "No snippets were found for operation #{operation} in"\
           "#{snippets_dir}"
      "No snippets found for operation::#{operation}"
    else
      do_read_snippets(snippets, parent, operation)
    end
  end

  def do_read_snippets(snippets, parent, operation)
    content = StringIO.new
    section_level = parent.level + 1
    section_id = parent.id
    snippets.each do |snippet|
      append_snippet_block(content, snippet, section_level, section_id,
                           operation)
    end
    content.string
  end

  def add_blocks(content, doc, parent)
    options = { safe: doc.options[:safe],
                attributes: { 'fragment' => '',
                              'projectdir' => doc.attr(:projectdir) } }
    fragment = Asciidoctor.load content, options
    fragment.blocks.each do |b|
      b.parent = parent
      parent << b
    end
  end

  def snippets_to_include(snippet_names, snippets_dir, operation)
    if snippet_names.empty?
      all_snippets snippets_dir, operation
    else
      snippet_names.split(',').map do |name|
        path = File.join snippets_dir, operation, "#{name}.adoc"
        Snippet.new(path, name)
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

  def append_snippet_block(content, snippet, section_level, section_id,
                           operation)
    write_title content, snippet, section_level, section_id
    write_content content, snippet, operation
  end

  def write_content(content, snippet, operation)
    if File.file? snippet.path
      content.puts File.readlines(snippet.path).join
    else
      warn "Snippet #{snippet.name} not found at #{snippet.path} for"\
           " operation #{operation}"
      content.puts "Snippet #{snippet.name} not found for"\
                   " operation::#{operation}"
      content.puts ''
    end
  end

  def write_title(content, snippet, level, id)
    section_level = '=' * (level + 1)
    content.puts "[[#{id}_#{snippet.name.sub '-', '_'}]]"
    content.puts "#{section_level} #{snippet.title}"
    content.puts ''
  end

  # Details of a snippet to be rendered
  class Snippet
    @titles = { 'http-request' => 'HTTP request',
                'curl-request' => 'Curl request',
                'httpie-request' => 'HTTPie request',
                'request-body' => 'Request body',
                'request-fields' => 'Request fields',
                'http-response' => 'HTTP response',
                'response-body' => 'Response body',
                'response-fields' => 'Response fields',
                'links' => 'Links' }

    class << self
      attr_reader :titles
    end

    attr_reader :name, :path

    def initialize(path, name)
      @path = path
      @name = name
    end

    def title
      Snippet.titles.fetch @name, name.sub('-', ' ').capitalize
    end
  end
end
