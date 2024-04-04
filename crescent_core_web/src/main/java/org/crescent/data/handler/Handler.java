package org.crescent.data.handler;

import org.crescent.index.entity.IndexingRequestForm;

public interface Handler {

	IndexingRequestForm handledData(String jsonFormStr);
}
