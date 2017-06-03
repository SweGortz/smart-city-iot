package org.gortz.greeniot.smartcityiot.model.formats.datatype;

import android.util.Log;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.gortz.greeniot.smartcityiot.dto.sensors.SensorNodeBaseMessage;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class DataStructureConverterTest {


    @Test
    public void convertDataSenML() throws Exception {
        PowerMockito.mockStatic(Log.class);
        DataStructureConverter dataStructure = new DataStructureConverter();
        SensorNodeBaseMessage s = dataStructure.convertData("[{\"bn\":\"urn:dev:mac:fcc23d000001856b;\",\"bt\":13232},{\"n\":\"no2;ADC\",\"u\":\"V\",\"v\":0.00}]","senML",null,null,null,null);
        Assert.assertEquals("urn:dev:mac:fcc23d000001856b;",s.getId());
        Assert.assertEquals(0.00,s.getData().get("ADC").get(0).getValue());
    }

}