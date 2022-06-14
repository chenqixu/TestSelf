package com.bussiness.bi.bigdata.json;

import java.io.File;
import java.io.OutputStream;

import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.QName;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XdmAtomicValue;
import net.sf.saxon.s9api.XsltCompiler;
import net.sf.saxon.s9api.XsltExecutable;
import net.sf.saxon.s9api.XsltTransformer;

public class XsltParseJson {
	
	public static void main(String[] args) throws Exception {
//		IJsonReaderTest1();
		
//		String outputFile = "";
//		File xsltFile = null;
////		JsonSaxWriter writer = new JsonSaxWriter(new FileOutputStream(outputFile));
//
//		SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
//		Schema schema = schemaFactory.newSchema(new StreamSource(XsltParseJson.class.getResourceAsStream("json.xsd")));
//		ValidatorHandler validatorHandler = schema.newValidatorHandler();
////		validatorHandler.setErrorHandler(new __ErrorHandler());
////		validatorHandler.setContentHandler(writer);
//
//		TransformerHandler transformerHandler = ((SAXTransformerFactory)TransformerFactory.newInstance())
//				.newTransformerHandler(new StreamSource(xsltFile));
//		transformerHandler.setResult(new SAXResult(validatorHandler));
//
////		JsonSaxReader reader = new JsonSaxReader();
////		reader.setContentHandler(transformerHandler);
////		reader.parse(new InputSource(inputFile.toString()));

//		File xsltFile = new File("j:/Work/海南/海南MRO/data/json_test1.data");
//		TransformerHandler transformerHandler = ((SAXTransformerFactory)TransformerFactory.newInstance())
//				.newTransformerHandler(new StreamSource(xsltFile));
		
//		// Create Transformer
//        TransformerFactory tf = TransformerFactory.newInstance();
//        StreamSource xslt = new StreamSource(
//                "src/blog/jaxbsource/xslt/stylesheet.xsl");
//        Transformer transformer = tf.newTransformer(xslt);
//        // Source
//        JAXBContext jc = JAXBContext.newInstance(XsltParseJson.class);
//        JAXBSource source = new JAXBSource(jc, catalog);
//        // Result
//        StreamResult result = new StreamResult(System.out);        
//        // Transform
//        transformer.transform(source, result);
		
		final String XSLT_PATH = "j:/Work/海南/海南MRO/data/json_test1.xsl";
        final String JSON = 
//        		"{\n" +
//                "    \"color\": \"red\",\n" +
//                "    \"value\": \"#f00\"\n" +
//                "}";
        		"{\n" +
                "     \"firstName\": \"John\",\n" +
                "     \"lastName\": \"Smith\",\n" +
                "     \"age\": 25,\n" +
                "     \"address\": {\n" +
                "         \"streetAddress\": \"21 2nd Street\",\n" +
                "         \"city\": \"New York\",\n" +
                "         \"state\": \"NY\",\n" +
                "         \"postalCode\": \"10021\"\n" +
                "     },\n" +
                "     \"phoneNumber\": [\n" +
                "         { \"type\": \"home\", \"number\": \"212 555-1234\" },\n" +
                "         { \"type\": \"fax\", \"number\": \"646 555-4567\" }\n" +
                "     ]\n" +
                "}";
        OutputStream outputStream = System.out;        
        Processor processor = new Processor(false);        
        Serializer serializer = processor.newSerializer();
        serializer.setOutputStream(outputStream);        
        XsltCompiler compiler = processor.newXsltCompiler();
        XsltExecutable executable = compiler.compile(new StreamSource(new File(XSLT_PATH)));        
        XsltTransformer transformer = executable.load();
        transformer.setInitialTemplate(new QName("init")); //<-- SET INITIAL TEMPLATE
        transformer.setParameter(new QName("jsonText"), new XdmAtomicValue(JSON)); //<-- PASS JSON IN AS PARAM
        transformer.setDestination(serializer);
        transformer.transform();
	}
}
