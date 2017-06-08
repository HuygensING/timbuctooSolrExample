# timbuctooSolrExample

## Presumed
- solr installed
- lucene installed

## Lucene

Make your filters in lucene, in this example (in analyzer):
- MyAccentFilterFactory
- MyAccentFilter

The other classes (MyAccentAnalyzer and MySearchAnalyzer) are only needed
for running 'stand alone'.

Add those first mentioned classes to a jar-file, in this example
'ProbeerLucene.jar'.

## Solr

Create your new solr core (in this example 'MyCore':
bin\solr create -c MyCore

This will create directory MyCore in {solr-home}\server\solr :
{solr-home}\server\solr\MyCore
In this new directory make a subdirectory 'lib'.
Copy your jar-file to this lib directory.

In MyCore you wil find directory 'conf'

Edit solrconfig.xml:
Search for '<lib dir="./lib" />' and uncomment this.

Also in 'conf':
According to the documentation there should be a schema.xml file. But it
usually is called managed-schema.
In fact, on starting solr it looks for schema.xml, strips the comments and
renames it maneged-schema. Schema.xml is saved as schema.xml.bak.
On the first run, it seems, the comments are not stripped.

You can either:
- edit managed-schema
or
- edit schema.xml
If you choose this last option, first rename managed-schema to schema.xml

What to add?
In schema.xml (or managed-schema) you will find alist of <field ...>
declarations.
Add your field(s) to this list.
In the example I have added:
<field name="author_tim" type="timbuctoo" indexed="true" stored="true" multiValued="true" />
Use any name you like as long as it is not yet in use.

If you want to add more than one but similar fields use an * in the name:
<dynamicField name="*_tim" type="timbuctoo" ...etc... />
In this case: use dynamicField instead of field! 

Also in schema.xml, a list of <fieldType ...> items.
The type in field refers to the fieldType name, in this example, type in
field is "timbuctoo", name in fieldType is also "timbuctoo":

    <fieldType name="timbuctoo" class="solr.TextField">
class refers to the used solr field type.
With in the fieldType we can add one or two <analyzer> tags:
- one: <analyzer> for both index and query;
- two: <analyzer type="index"> and <analyzer type="query"> when different
  approaches for indexing and querying are needed.

In our example:
    <fieldType name="timbuctoo" class="solr.TextField">
	<analyzer type="index">
	    <tokenizer class="solr.StandardTokenizerFactory"/>
	    <filter class="nl.knaw.huygens.timbuctoo.lucene.analyzer.MyAccentFilterFactory" />
	</analyzer>
	<analyzer type="query">
	    <tokenizer class="solr.StandardTokenizerFactory"/>
	</analyzer>
    </fieldType>

First the tokenizer, use one of the provided one (StandardTokenizer splits
one white space), or define your own (not in this example).
After the tokenizer, zero or more filters. Filters provide a 'pipeline'
the output of one filter can be the input to the next.

In this example the indexer will use 'MyAccentFilter' (provided in the
jar-file).
The queryer will just split the input.



