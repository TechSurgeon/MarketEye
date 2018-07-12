package com.sa.marketslayer.library.parsers.interfaces;

import java.util.Map;

import com.sa.marketslayer.library.databeans.Security;

public interface DataParser {

	public Map<String, Security> updateSymbolsGetDiff(Map<String, Security> secs);
}
