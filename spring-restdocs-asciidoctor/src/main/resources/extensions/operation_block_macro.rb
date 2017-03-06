require 'asciidoctor/extensions'
require 'stringio'

# Spring REST Docs block macro to import multiple snippet of an operation at once
#
# Usage
#
#   operation::operation-name[snippets='snippet-name1,snippet-name2', level=<indentation level>]
#
class OperationBlockMacro < Asciidoctor::Extensions::BlockMacroProcessor
  use_dsl
  named :operation

  def initialize name, config
    super
    # pre-defined section titles for commonly used snippets
    @titles = {:'http-request' => 'HTTP request',
               :'curl-request' => 'curl request',
               :'httpie-request' => 'HTTPie request',
               :'request-body' => 'Request body',
               :'request-fields' => 'Request fields',
               :'http-response' => 'HTTP response',
               :'response-body' => 'Response body',
               :'response-fields' => 'Response fields',
               :'links' => 'Links'
    }
  end

  def process(parent, reader, attrs)
    doc = parent.document
    snippet_dir = doc.attributes['snippets']
    snippets = snippets_to_include(attrs, snippet_dir, reader)
    section_level = parent.level + 1

    params = {:snippet_dir => snippet_dir,
              :section_level => section_level,
              :operation => reader}

    content = StringIO.new
    snippets.each do |snippet|
      append_snippet_block(content, snippet, params)
    end

    add_snippets_block(content, doc, parent) unless content.length == 0
    nil
  end

  def add_snippets_block(content, doc, parent)
    fragment = Asciidoctor.load content,
                                safe: doc.options[:safe],
                                attributes: {'fragment' => '', 'projectdir' => doc.attr(:projectdir)}
    fragment.blocks.each do |b|
      b.parent = parent
      parent << b
    end
  end

  def snippets_to_include(attrs, snippet_dir, operation)
    if not attrs['snippets'].to_s.empty?
      snippets_from_attribute attrs
    else
      all_snippets snippet_dir, operation
    end
  end

  def snippets_from_attribute(attrs)
    attrs.fetch('snippets').split(',')
  end

  def all_snippets(snippet_dir, operation)
    all_snippet_file_names = []
    Dir.entries(File.join(snippet_dir.to_s, operation)).sort.select { |file|
      if file.end_with? '.adoc'
        file.slice!('.adoc')
        all_snippet_file_names << file
      end
    }
    all_snippet_file_names
  end

  def append_snippet_block(content, snippet, params)
    write_title content, snippet, params[:section_level]
    write_content content, snippet, params
  end

  def write_content(content, snippet, params)
    snippet_path = File.join(params[:snippet_dir].to_s, params[:operation], "#{snippet}.adoc")
    content.puts File.readlines(snippet_path).join

  rescue Errno::ENOENT
    content.puts "WARNING: snippet not found: #{snippet_path}"
    add_new_line content
  end

  def write_title(content, snippet, level)
    # an asciidoctor level is always an equal
    # sign more than the level number
    section_level = '=' * (level + 1)
    content.puts "#{section_level} #{title(snippet)}"
    add_new_line content
  end

  def title(snippet)
    (@titles[snippet.to_sym] || title_from_file_name(snippet))
  end

  def add_new_line(content)
    content.puts ''
  end

  def title_from_file_name(snippet)
    snippet.sub('-', ' ').capitalize
  end

end
