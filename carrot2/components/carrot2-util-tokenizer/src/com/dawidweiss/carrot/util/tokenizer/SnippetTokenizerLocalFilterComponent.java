/*
 * Carrot2 Project
 * Copyright (C) 2002-2005, Dawid Weiss
 * Portions (C) Contributors listed in carrot2.CONTRIBUTORS file.
 * All rights reserved.
 *
 * Refer to the full license file "carrot2.LICENSE"
 * in the root folder of the CVS checkout or at:
 * http://www.cs.put.poznan.pl/dweiss/carrot2.LICENSE
 */
package com.dawidweiss.carrot.util.tokenizer;

import java.util.*;

import com.dawidweiss.carrot.core.local.*;
import com.dawidweiss.carrot.core.local.clustering.*;
import com.dawidweiss.carrot.core.local.profiling.*;

/**
 * Note: there is no support for tokenizing document content yet.
 * 
 * TODO: Refactor SnippetTokenizer and delegate calls from this filter to it
 * 
 * @author Stanislaw Osinski
 * @version $Revision$
 */
public class SnippetTokenizerLocalFilterComponent extends
    ProfiledLocalFilterComponentBase implements RawDocumentsConsumer,
    TokenizedDocumentsProducer, LocalFilterComponent
{
    /** Capabilities required from the previous component in the chain */
    private final static Set CAPABILITIES_PREDECESSOR = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsProducer.class
        }));

    /** This component's capabilities */
    private final static Set CAPABILITIES_COMPONENT = new HashSet(Arrays
        .asList(new Object []
        {
            RawDocumentsConsumer.class, TokenizedDocumentsProducer.class
        }));

    /** Capabilities required from the next component in the chain */
    private final static Set CAPABILITIES_SUCCESSOR = new HashSet(Arrays
        .asList(new Object []
        {
            TokenizedDocumentsConsumer.class
        }));

    /** Tokenized documents consumer */
    private TokenizedDocumentsConsumer tokenizedDocumentsConsumer;

    /** */
    private SnippetTokenizer snippetTokenizer;

    /** */
    private RequestContext requestContext;
    
    /** */
    public final static String PARAMETER_MAX_TOKENS_TO_READ = "max-tokens";

    public void init(LocalControllerContext context)
        throws InstantiationException
    {
        super.init(context);
        snippetTokenizer = new SnippetTokenizer();
    }

    public void startProcessing(RequestContext requestContext)
        throws ProcessingException
    {
        super.startProcessing(requestContext);
        this.requestContext = requestContext;
    }

    public void addDocument(RawDocument doc) throws ProcessingException
    {
        startTimer();
        final Integer maxTokens = (Integer) requestContext
            .getRequestParameters().get(PARAMETER_MAX_TOKENS_TO_READ);

        final TokenizedDocument tokenizedDocument;
        if (maxTokens != null)
        {
            tokenizedDocument = snippetTokenizer.tokenize(doc, maxTokens.intValue());
        }
        else
        {
            tokenizedDocument = snippetTokenizer.tokenize(doc);
        }

        stopTimer();

        tokenizedDocumentsConsumer.addDocument(tokenizedDocument);
    }

    public Set getComponentCapabilities()
    {
        return CAPABILITIES_COMPONENT;
    }

    public Set getRequiredSuccessorCapabilities()
    {
        return CAPABILITIES_SUCCESSOR;
    }

    public Set getRequiredPredecessorCapabilities()
    {
        return CAPABILITIES_PREDECESSOR;
    }

    public void setNext(LocalComponent next)
    {
        super.setNext(next);
        tokenizedDocumentsConsumer = (TokenizedDocumentsConsumer) next;
    }

    public void flushResources()
    {
        super.flushResources();
        tokenizedDocumentsConsumer = null;
        profile = null;
        requestContext = null;

        snippetTokenizer.clear();
    }

    public String getName()
    {
        return "Tokenizer";
    }
}