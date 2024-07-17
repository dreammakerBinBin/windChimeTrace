package com.windchime.boot.config;

import com.windchime.boot.pojo.windchimetraceinfo.WindChimeTraceInfo;

public interface WindChimeTraceServicePersistence {
    public void saveWindChimeTraceInfo(WindChimeTraceInfo windChimeTraceInfo, String headersString);
    public void refreshTraceConfig();
}
