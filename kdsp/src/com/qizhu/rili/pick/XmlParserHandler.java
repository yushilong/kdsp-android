package com.qizhu.rili.pick;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by  Rex on 2016/10/20.
 */
public class XmlParserHandler extends DefaultHandler {

    /**
     * 存储所有的解析对象
     */
    private List<ProvinceModel> provinceList = new ArrayList<ProvinceModel>();

    public XmlParserHandler() {
    }

    public List<ProvinceModel> getDataList() {
        return provinceList;
    }

    @Override
    public void startDocument() throws SAXException {
        // 当读到第一个开始标签的时候，会触发这个方法
    }

    ProvinceModel provinceModel = new ProvinceModel();
    CityModel cityModel = new CityModel();
    DistrictModel districtModel = new DistrictModel();
    private String currentValue;
    private String currentTag;


    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // 当遇到开始标记的时候，调用这个方法
        if (qName.equals("province")) {
            provinceModel = new ProvinceModel();
            provinceModel.setName(attributes.getValue(0));
            provinceModel.setCityList(new ArrayList<CityModel>());
        } else if (qName.equals("city")) {
            cityModel = new CityModel();
            cityModel.setName(attributes.getValue(0));
            cityModel.setDistrictList(new ArrayList<DistrictModel>());
        } else if (qName.equals("area")) {
            districtModel = new DistrictModel();
            currentTag = qName;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // 遇到结束标记的时候，会调用这个方法
        if (qName.equals("area")) {

            cityModel.getDistrictList().add(districtModel);
        } else if (qName.equals("city")) {
            provinceModel.getCityList().add(cityModel);
        } else if (qName.equals("province")) {
            provinceList.add(provinceModel);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        currentValue = new String(ch,start,length);
        if(currentValue != null && !"".equals(currentValue) && !"\n".equals(currentValue)) {
            if("area".equals(currentTag)){
                districtModel.setName(currentValue.toString().trim());
            }

        }
        currentTag = null;
        currentValue = null;
    }
}
