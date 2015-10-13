package com.blackwhitesoftware.pandalight.remote_control

import groovy.mock.interceptor.MockFor

/**
 * Created by hudini on 11.10.2015.
 */
class SerialConnectionReaderTest extends GroovyTestCase {
    void testRun()
    {
        def stream = new ByteArrayInputStream(new byte[0])
        def adapterMock = new MockFor(ConnectionAdapter)
        adapterMock.demand.gotLine(0) {}
        adapterMock.use({
            def reader = new SerialConnectionReader(stream, new ConnectionAdapter() {})
            reader.run()
        })

        byte[] buffer = new byte[1] {
            (byte) 1
        }
        stream = new ByteArrayInputStream()
    }
}
