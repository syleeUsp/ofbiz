/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.conversion.JSONConverters;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.StringUtil;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilGenerics;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.base.util.string.FlexibleStringExpander;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.entity.util.EntityQuery;
import org.apache.ofbiz.entity.util.EntityUtil;
import org.apache.ofbiz.entity.util.EntityUtilProperties;
import org.apache.ofbiz.party.contact.ContactMechWorker;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;
import org.jdom.JDOMException;
import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;

public class PurchaseServices {

    public static final String module = PurchaseServices.class.getName();
    public static final String resourceError = "PurchaseUiLabels";
    public static final String resource = "PurchaseUiLabels";
    private static int pdfCount = 0;
    private static String pdfPath;

    public static Map<String, Object> RUPoList(DispatchContext dctx, Map<String, ?> context) {
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
        Map<String, Object> result = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");

        String crudMode = context.get("crudMode") == null ? "" : (String) context.get("crudMode");
    	String searchPoNo = context.get("searchPoNo") == null ? "" : (String) context.get("searchPoNo");
    	Timestamp searchOrderFromDate = (Timestamp) context.get("searchOrderFromDate");
    	Timestamp searchOrderToDate = UtilDateTime.getDayEnd((Timestamp) context.get("searchOrderToDate"));
        String searchVendorId = context.get("searchVendorId") == null ? "" : (String) context.get("searchVendorId");
        String searchCustomerId = context.get("searchCustomerId") == null ? "" : (String) context.get("searchCustomerId");
        String searchPort = context.get("searchPort") == null ? "" : (String) context.get("searchPort");
        String searchGrade = context.get("searchGrade") == null ? "" : (String) context.get("searchGrade");
        String searchCoatingWeight = context.get("searchCoatingWeight") == null ? "" : (String) context.get("searchCoatingWeight");
        String searchSurfaceCoilType = context.get("searchSurfaceCoilType") == null ? "" : (String) context.get("searchSurfaceCoilType");
        String searchGauge = context.get("searchGauge") == null ? "" : (String) context.get("searchGauge");
        String searchWidth = context.get("searchWidth") == null ? "" : (String) context.get("searchWidth");
        String draw = "";

        List<Map<String, Object>> resultList = new LinkedList<Map<String,Object>>();
        if (userLogin != null) {
        	String userLoginId = (String) userLogin.get("userLoginId");

	        try {
	        	// PO Management List 화면 읽기
	        	if("R".equals(crudMode)) {
		        	List<EntityCondition> poConditionList = UtilMisc.<EntityCondition>toList(
		                    EntityCondition.makeCondition("orderDate", EntityOperator.GREATER_THAN_EQUAL_TO, searchOrderFromDate),
		                    EntityCondition.makeCondition("orderDate", EntityOperator.LESS_THAN_EQUAL_TO, searchOrderToDate));

		        	if(!"".equals(searchPoNo)) {
		        		poConditionList.add(EntityCondition.makeCondition("poNo", EntityOperator.IN, searchPoNo));
		        	}
		        	if(!"".equals(searchVendorId)) {
		        		poConditionList.add(EntityCondition.makeCondition("vendorId", EntityOperator.EQUALS, searchVendorId));
		        	}

		            EntityCondition poListCondition = EntityCondition.makeCondition(poConditionList, EntityOperator.AND);

		        	List<GenericValue> poMasterList = EntityQuery.use(delegator)
				               .from("PoMaster")
				               .where(poListCondition)
				               .queryList();

		        	Map<String, Object> conditionMap = new HashMap<String, Object>();
					if(!"".equals(searchCustomerId)) {
						conditionMap.put("customerId", searchCustomerId);
					}
					if(!"".equals(searchPort)) {
						conditionMap.put("port", searchPort);
					}
					if(!"".equals(searchGrade)) {
						conditionMap.put("grade", searchGrade);
					}
					if(!"".equals(searchCoatingWeight)) {
						conditionMap.put("coatingWeight", searchCoatingWeight);
					}
					if(!"".equals(searchSurfaceCoilType)) {
						conditionMap.put("surfaceCoilType", searchSurfaceCoilType);
					}
					if(!"".equals(searchGauge)) {
						conditionMap.put("gauge", searchGauge);
					}
					if(!"".equals(searchWidth)) {
						conditionMap.put("width", searchWidth);
					}

		        	for(GenericValue poMasterInfo : poMasterList) {
		        		List<GenericValue> referenceList = poMasterInfo.getRelated("Reference", conditionMap, UtilMisc.toList("poNo","lotNo"), false);
		        		if(referenceList.size() > 0) {
			        		for(GenericValue referenceInfo : referenceList) {
			        			Map<String, Object> resultMap = new HashMap<String, Object>();
			            		resultMap.putAll(poMasterInfo);
			            		resultMap.putAll(referenceInfo);
			            		resultList.add(resultMap);
			        		}
		        		}
		        	}
	        	// PO Management List 화면에서 poStatus값 update
	        	} else if("SU".equals(crudMode)) {
	        		String reqData = (String) context.get("reqData");
	        		JSONArray data = new JSONArray(reqData);//jsonarray 형태로
	                Map<String, Object> resultMap = new HashMap<String, Object>();

	                GenericValue createNUpdatePoInfo = delegator.makeValue("PoMaster");
	                GenericValue createNUpdateReferenceInfo = delegator.makeValue("Reference");
	                if(data.length() > 0) {
	                	for(int i=0 ; data.length() > i ; i++) {
	                		JSONObject jsonobj = data.getJSONObject(i);

	                		createNUpdatePoInfo.set("poNo", jsonobj.getString("poNo"));
	                		createNUpdatePoInfo.set("poStatus", jsonobj.getString("poStatus"));
    	                	createNUpdatePoInfo.set("lastUpdateUserId", userLoginId);
    	                	createNUpdatePoInfo.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
    	                	createNUpdatePoInfo.set("lastUpdatedTxStamp", UtilDateTime.nowTimestamp());
	    	                createNUpdatePoInfo = delegator.createOrStore(createNUpdatePoInfo);

	    	                Map<String, Object> conditionMap = new HashMap<String, Object>();
	    	                conditionMap.put("poNo", jsonobj.getString("poNo"));
	    	                List<GenericValue> referenceList = createNUpdatePoInfo.getRelated("Reference", conditionMap, null, false);
			        		if(referenceList.size() > 0) {
				        		for(GenericValue referenceInfo : referenceList) {
				        			createNUpdateReferenceInfo.set("referenceSeq", referenceInfo.getLong("referenceSeq"));
			    	                createNUpdateReferenceInfo.set("lastUpdateUserId", userLoginId);
			    	                createNUpdateReferenceInfo.set("lastUpdatedStamp", UtilDateTime.nowTimestamp());
			    	                createNUpdateReferenceInfo.set("lastUpdatedTxStamp", UtilDateTime.nowTimestamp());
			    	                createNUpdateReferenceInfo = delegator.createOrStore(createNUpdateReferenceInfo);
			    	                createNUpdateReferenceInfo.clear();
				        		}
			        		}

	    	                resultMap.putAll(createNUpdateReferenceInfo);
	    	                resultMap.putAll(createNUpdatePoInfo);
	    	                resultList.add(resultMap);
	    	                createNUpdatePoInfo.clear();
	                	}
	                }
	        	}
	        } catch (GenericEntityException e){
	            Debug.logError(e, "Cannot CRUDPoList ", module);
	        }
        }
        result.put("data", resultList);
        result.put("draw", draw);
        result.put("recordsTotal", resultList.size());
        result.put("recordsFiltered", resultList.size());

        return result;
    }

    public static Map<String, Object> CRUPoList(DispatchContext dctx, Map<String, ?> context) {
    	Delegator delegator = dctx.getDelegator();
    	Locale locale = (Locale) context.get("locale");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = (String) userLogin.get("userLoginId");

        String crudMode = context.get("crudMode") == null ? "" : (String) context.get("crudMode");
    	String poNo = context.get("poNo") == null ? "" : (String) context.get("poNo");
    	String reqData = context.get("reqData") == null ? "" : (String) context.get("reqData");

    	Map<String, Object> result = ServiceUtil.returnSuccess();
        List<Map<String, Object>> resultList = new LinkedList<Map<String,Object>>();
        if (userLogin != null) {
	        try {
	        	// PO Management List 화면에서 PONO로 edit화면 open시
	        	// onload할때 그리드 리스트 호출
	        	if("UR".equals(crudMode)) {
		        	GenericValue poMasterInfo = EntityQuery.use(delegator)
				               .from("PoMaster")
				               .where("poNo", poNo)
				               .queryOne();

		        	Map<String, Object> conditionMap = new HashMap<String, Object>();
					conditionMap.put("poNo", poNo);
	        		List<GenericValue> referenceList = poMasterInfo.getRelated("Reference", conditionMap, null, false);
	        		if(referenceList.size() > 0) {
		        		for(GenericValue referenceInfo : referenceList) {
		        			Map<String, Object> resultMap = new HashMap<String, Object>();
		            		resultMap.putAll(poMasterInfo);
		            		resultMap.putAll(referenceInfo);
		            		resultList.add(resultMap);
		        		}
	        		}
	        	// 기존 PO 복제 onload 시 사용
	        	} else if("CL".equals(crudMode)) {
		        	GenericValue poMasterInfo = EntityQuery.use(delegator)
				               .from("PoMaster")
				               .where("poNo", poNo)
				               .queryOne();

		        	Map<String, Object> conditionMap = new HashMap<String, Object>();
					conditionMap.put("poNo", poNo);
	        		List<GenericValue> referenceList = poMasterInfo.getRelated("Reference", conditionMap, null, false);
	        		if(referenceList.size() > 0) {
		        		for(GenericValue referenceInfo : referenceList) {
		        			Map<String, Object> resultMap = new HashMap<String, Object>();
		            		resultMap.putAll(poMasterInfo);
		            		resultMap.putAll(referenceInfo);
		            		resultList.add(resultMap);
		        		}
	        		}
	        	// PO번호 입력 시 중복체크
	        	} else if("DP".equals(crudMode)) {
		        	GenericValue poMasterInfo = EntityQuery.use(delegator)
				               .from("PoMaster")
				               .where("poNo", poNo)
				               .queryOne();
            		resultList.add(poMasterInfo);
	        	// PO create 및 update 시 호출
	        	} else if("C".equals(crudMode) || "U".equals(crudMode)) {
	        		Map<String, Object> resultMap = new HashMap<String, Object>();
	        		GenericValue createNUpdatePoInfo = delegator.makeValue("PoMaster");
	        		createNUpdatePoInfo.setPKFields(context);
	        		createNUpdatePoInfo.setNonPKFields(context);
	        		createNUpdatePoInfo = delegator.createOrStore(createNUpdatePoInfo);

	        		resultMap.putAll(createNUpdatePoInfo);
	                resultList.add(resultMap);

	        		GenericValue createNUpdateReferenceInfo = delegator.makeValue("Reference");
	    		    JSONArray data = new JSONArray(reqData);//jsonarray 형태로

	    		    int resultInt = 0;
	    		    if(data.length() > 0) {
	    		    	long seqNum = 0L;
	    		    	long ppglNo = 0L;
	        			for(int i=0 ; data.length() > i ; i++) {
	        				Map<String, Object> referenceMap = new HashMap<String, Object>();
	        				referenceMap.put("poNo", createNUpdatePoInfo.getString("poNo"));
	                		JSONObject jsonobj = data.getJSONObject(i);

	                		Iterator<String> keysItr = jsonobj.keys();
	                	    while(keysItr.hasNext()) {
	                	        String key = keysItr.next();
	                	        Object value = new Object();
	                	        if(jsonobj.getString(key) != null && !"".equals(jsonobj.getString(key))) {
	                	        	if("unitQuantity".equals(key)) {
	                	        		String str = jsonobj.getString(key);
	                	        		long lStr = Long.valueOf(str.replaceAll(",", ""));
	                	        		value = BigDecimal.valueOf(lStr);
	                	        	} else if("unitCost".equals(key) || "civAmount".equals(key)
		                	        		|| "loadedQty".equals(key) || "weight".equals(key)
		                	        		|| "coilQuantity".equals(key)) {
		                	        	long str = jsonobj.getLong(key);
		                	        	value = BigDecimal.valueOf(str);
		                	        } else if("unitPrice".equals(key) || "commissionUnitPrice".equals(key)) {
		                	        	String str = jsonobj.getString(key);
		                	        	double dbl = Double.valueOf(str.replaceAll(",", ""));
		                	        	value = BigDecimal.valueOf(dbl);
		                	        } else if("etd".equals(key) || "eta".equals(key) || "blDate".equals(key)) {
		                	        	value = (Timestamp) jsonobj.get(key);
		                	        } else {
		                	        	value = jsonobj.getString(key);
		                	        }
		                	        referenceMap.put(key, value);
	                	        } else if("".equals(jsonobj.getString(key))){
	                	        	referenceMap.put(key, null);
	                	        }
	                	    }

	                	    if(jsonobj.isNull("referenceSeq") || "".equals(jsonobj.get("referenceSeq"))) {
	                	    	if(seqNum == 0L) {
	                	    		GenericValue seqNumInfo = EntityQuery.use(delegator).select("referenceSeq").from("Reference").orderBy("referenceSeq DESC").queryFirst();
	                	    		if(seqNumInfo != null) {
	                	    			seqNum = seqNumInfo.getLong("referenceSeq");
	                	    		} else {
	                	    			seqNum = 0;
	                	    		}
		                	    }

	                	    	seqNum = seqNum + 1;
	                	    	Map<String, Long> sequenceNum = UtilMisc.toMap("referenceSeq", seqNum);
		                	    referenceMap.putAll(sequenceNum);
	                	    } else {
	                	    	referenceMap.put("referenceSeq", jsonobj.getLong("referenceSeq"));
	                	    	GenericValue referenceInfo = EntityQuery.use(delegator)
		            		               .from("Reference")
		            		               .where("referenceSeq", referenceMap.get("referenceSeq"))
		            		               .queryOne();
	                			referenceMap.put("createUserId", referenceInfo.getString("createUserId"));
		                		referenceMap.put("createdStamp", referenceInfo.getTimestamp("createdStamp"));
		                		referenceMap.put("createdTxStamp", referenceInfo.getTimestamp("createdTxStamp"));
	                	    }

	                	    if(jsonobj.isNull("ppglNo") || "".equals(jsonobj.get("ppglNo"))) {
	                	    	if(ppglNo == 0L) {
	                	    		GenericValue ppglNoInfo = EntityQuery.use(delegator).select("ppglNo").from("Reference")
		                	    			.where("lotNo", referenceMap.get("lotNo"), "poNo", referenceMap.get("poNo"), "referenceNo", referenceMap.get("referenceNo")).orderBy("ppglNo DESC").queryFirst();
	                	    		if(ppglNoInfo != null) {
	                	    			ppglNo = ppglNoInfo.getLong("ppglNo");
	                	    		} else {
	                	    			ppglNo = 0;
	                	    		}
		                	    }
		                	    ppglNo = ppglNo + 1;
		                	    Map<String, Long> ppglNumVal = UtilMisc.toMap("ppglNo", ppglNo);
		                	    referenceMap.putAll(ppglNumVal);
	                	    } else {
	                	    	referenceMap.put("ppglNo", jsonobj.getLong("ppglNo"));
	                	    }

	                	    if("C".equals(crudMode)) {
		                		referenceMap.put("createUserId", userLoginId);
		                		referenceMap.put("createdStamp", UtilDateTime.nowTimestamp());
		                		referenceMap.put("createdTxStamp", UtilDateTime.nowTimestamp());
	                		}

	                		referenceMap.put("lastUpdateUserId", userLoginId);
	                		referenceMap.put("lastUpdatedStamp", UtilDateTime.nowTimestamp());
	    	                referenceMap.put("lastUpdatedTxStamp", UtilDateTime.nowTimestamp());
	    	                createNUpdateReferenceInfo.setPKFields(referenceMap);
	    	                createNUpdateReferenceInfo.setNonPKFields(referenceMap);
	    	                createNUpdateReferenceInfo = delegator.createOrStore(createNUpdateReferenceInfo);

	    	                resultMap.putAll(createNUpdateReferenceInfo);
	    	                resultList.add(resultMap);

	    	                createNUpdateReferenceInfo.clear();
	    	                resultInt++;
	        			}
	    		    }

	                createNUpdatePoInfo.clear();
	                if(resultInt > 0) {
	    		    	result.put("successStr", "success");
	    		    } else {
	    		    	result.put("successStr", "fail");
	    		    }
	            // PO Reference 삭제 시 호출
	        	} else if("D".equals(crudMode)) {
	        		GenericValue createNUpdateReferenceInfo = delegator.makeValue("Reference");
	    		    JSONArray data = new JSONArray(reqData);//jsonarray 형태로
	    		    int resultInt = 0;
	    		    if(data.length() > 0) {
	        			for(int i=0 ; data.length() > i ; i++) {
	                		JSONObject jsonobj = data.getJSONObject(i);
	                		createNUpdateReferenceInfo.set("referenceSeq", jsonobj.getLong("referenceSeq"));
	                		resultInt += delegator.removeByAnd("Reference", createNUpdateReferenceInfo);
	        			}
	    		    }
	    		    if(resultInt > 0) {
	    		    	result.put("successStr", "success");
	    		    } else {
	    		    	result.put("successStr", "fail");
	    		    }
	        	}
	        } catch (GenericEntityException e){
	            Debug.logError(e, "Cannot CRUDPoList ", module);
	        }
        }
        result.put("data", resultList);
        result.put("recordsTotal", resultList.size());
        result.put("recordsFiltered", resultList.size());

        return result;
    }

    public static Map<String, Object> searchVendor(DispatchContext dctx, Map<String, ?> context) {
    	Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        try {
            // Check if the country is a country group and get recursively the
            // states
        	Map<String, Object> vendorInfo = new HashMap<String, Object>();
        	vendorInfo = EntityQuery.use(delegator)
		               .from("Vendor")
		               .where("vendorId", context.get("vendorId"))
		               .queryOne();

        	if(vendorInfo == null) {
        		vendorInfo = new HashMap<String, Object>();
        		result.put("vendorInfo", vendorInfo);
        		result.put("resultState", "fail");
        	} else {
        		result.put("vendorInfo", vendorInfo);
        		result.put("resultState", "success");
        	}

        } catch (GenericEntityException e){
            Debug.logError(e, "Cannot lookup Vendor ", module);
        }

        return result;
    }

    public static Map<String, Object> searchCustomer(DispatchContext dctx, Map<String, ?> context) {
    	Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        try {
            // Check if the country is a country group and get recursively the
            // states
        	Map<String, Object> customerInfo = new HashMap<String, Object>();
        	customerInfo = EntityQuery.use(delegator)
		               .from("Customer")
		               .where("customerId", context.get("customerId"))
		               .queryOne();
        	if(customerInfo == null) {
        		customerInfo = new HashMap<String, Object>();
        		result.put("customerInfo", customerInfo);
        		result.put("resultState", "fail");
        	} else {
        		result.put("customerInfo", customerInfo);
        		result.put("resultState", "success");
        	}

        } catch (GenericEntityException e){
            Debug.logError(e, "Cannot lookup Customer ", module);
        }

        return result;
    }

    public static Map<String, Object> searchPo(DispatchContext dctx, Map<String, ?> context) {
    	Delegator delegator = dctx.getDelegator();
        Map<String, Object> result = ServiceUtil.returnSuccess();

        try {
            // Check if the country is a country group and get recursively the
            // states
        	List<GenericValue> poMasterList = EntityQuery.use(delegator)
		               .from("Reference")
		               .where("poNo", context.get("poNo"), "lotNo", context.get("lotNo"))
		               .queryList();
        	if(poMasterList == null) {
        		poMasterList.clear();
        		result.put("poInfo", poMasterList);
        		result.put("resultState", "fail");
        	} else {
        		result.put("poInfo", poMasterList);
        		result.put("resultState", "success");
        	}

        } catch (GenericEntityException e){
            Debug.logError(e, "Cannot lookup PoMaste ", module);
        }

        return result;
    }

    public static Map<String, Object> multipleUploadShippingDoc(DispatchContext dctx, Map<String, ? extends Object> context)
    throws IOException, JDOMException {

        Map<String, Object> result = new HashMap<String, Object>();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Delegator delegator = dctx.getDelegator();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String poNo = (String) context.get("poNo");
        poNo = poNo.trim();
        String lotNo = (String) context.get("lotNo");
        lotNo = lotNo.trim();
        ByteBuffer pdfData = (ByteBuffer) context.get("uploadedFile");
        String uploadFileName = (String) context.get("_uploadedFile_fileName");
//        String imageResize = (String) context.get("imageResize");
        Locale locale = (Locale) context.get("locale");

        String pdfFilenameFormat = "";
        String pdfServerPath = "";
        String pdfServerUrl = "";
        String rootTargetDirectory = "";
        String pdfName = "";
        String pdfUrl = "";
        if (UtilValidate.isNotEmpty(uploadFileName)) {
            pdfFilenameFormat = EntityUtilProperties.getPropertyValue("purchase", "pdf.filename.format", delegator);
            pdfServerPath = FlexibleStringExpander.expandString(EntityUtilProperties.getPropertyValue("purchase", "pdf.server.path", delegator), context);
            pdfServerUrl = FlexibleStringExpander.expandString(EntityUtilProperties.getPropertyValue("purchase", "pdf.url.prefix", delegator), context);
            rootTargetDirectory = pdfServerPath;
            File rootTargetDir = new File(rootTargetDirectory);
            if (!rootTargetDir.exists()) {
                boolean created = rootTargetDir.mkdirs();
                if (!created) {
                    String errMsg = UtilProperties.getMessage(resourceError, "ProductCannotCreateTheTargetDirectory", locale);
                    Debug.logFatal(errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }

            // Create folder product id.
            String targetDirectory = pdfServerPath + "/" + poNo + "/" + lotNo;
            File targetDir = new File(targetDirectory);
            if (!targetDir.exists()) {
                boolean created = targetDir.mkdirs();
                if (!created) {
                    String errMsg = "Cannot create the target directory";
                    Debug.logFatal(errMsg, module);
                    return ServiceUtil.returnError(errMsg);
                }
            }

            File file = new File(pdfServerPath + "/" + poNo + "/" + lotNo + "/" + uploadFileName);
            pdfPath = pdfServerPath + "/" + poNo + "/" + lotNo + "/" + uploadFileName;
            file = checkExistsPdf(file);
            if (UtilValidate.isNotEmpty(file)) {
                pdfName = file.getPath();
                pdfName = pdfName.substring(pdfName.lastIndexOf(File.separator) + 1);
            }

            // Create image file original to folder product id.
            try {
                RandomAccessFile out = new RandomAccessFile(file, "rw");
                out.write(pdfData.array());
                out.close();
            } catch (FileNotFoundException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                        "PurchasePDFViewUnableWriteFile", UtilMisc.toMap("fileName", file.getAbsolutePath()), locale));
            } catch (IOException e) {
                Debug.logError(e, module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError,
                        "PurchasePDFViewUnableWriteBinaryData", UtilMisc.toMap("fileName", file.getAbsolutePath()), locale));
            }

            pdfUrl = pdfServerUrl + "/" + poNo + "/" + lotNo + "/" + pdfName;
        }

        try {
        	List<GenericValue> referenceInfoList = EntityQuery.use(delegator)
	    		.from("Reference")
	    		.where(
	    			"poNo", poNo,
	    			"lotNo", lotNo
    		).queryList();

        	if(referenceInfoList.size() > 0) {
        		for(GenericValue referenceInfo : referenceInfoList) {
        			referenceInfo.put("lastUpdateUserId", userLogin.get("userLoginId"));
        			referenceInfo.put("lastUpdatedStamp", UtilDateTime.nowTimestamp());
        			referenceInfo.put("lastUpdatedTxStamp", UtilDateTime.nowTimestamp());

        			String docClass = (String) context.get("docClass");
        			docClass = docClass.trim();
        			if("BL".equals(docClass)) {
        				String blDocFileYN = (String)context.get("blDocFileYN") == null ? "" : (String)context.get("blDocFileYN");
        				String vessel = (String)context.get("vessel") == null ? "" : (String)context.get("vessel");
        				String blNo = (String)context.get("blNo") == null ? "" : (String)context.get("blNo");
        				String portOfLoading = (String)context.get("portOfLoading") == null ? "" : (String)context.get("portOfLoading");
        				String shippingCarrier = (String)context.get("shippingCarrier") == null ? "" : (String)context.get("shippingCarrier");

        				referenceInfo.put("blDocFileYN", context.get("blDocFileYN"));
        				referenceInfo.put("blDocFilePath", pdfUrl);
        				referenceInfo.put("vessel", context.get("vessel"));
        				referenceInfo.put("blNo", context.get("blNo"));
        				if(context.get("blDate") != null) {
        					referenceInfo.put("blDate", context.get("blDate"));
        				}
        				referenceInfo.put("portOfLoading", portOfLoading);
        				referenceInfo.put("shippingCarrier", shippingCarrier);
        			} else if("CI".equals(docClass)) {
        				String ciDocFileYN = (String)context.get("ciDocFileYN") == null ? "" : (String)context.get("ciDocFileYN");
        				String contractNo = (String)context.get("contractNo") == null ? "" : (String)context.get("contractNo");
        				String unitCost = (String)context.get("unitCost") == null ? "" : (String)context.get("unitCost");
        				String civAmount = (String)context.get("civAmount") == null ? "" : (String)context.get("civAmount");

        				referenceInfo.put("ciDocFileYN", ciDocFileYN);
        				referenceInfo.put("ciDocFilePath", pdfUrl);
        				referenceInfo.put("contractNo", context.get("contractNo"));
        				referenceInfo.put("unitCost", context.get("unitCost"));
        				referenceInfo.put("civAmount", context.get("civAmount"));
        			} else if("PL".equals(docClass)) {


        				referenceInfo.put("plDocFileYN", context.get("plDocFileYN"));
        				referenceInfo.put("plDocFilePath", pdfUrl);
        				referenceInfo.put("loadedQty", context.get("loadedQty"));
        				referenceInfo.put("weight", context.get("weight"));
        				referenceInfo.put("linealFeet", context.get("linealFeet"));
        				referenceInfo.put("coilQty", context.get("coilQty"));
        				referenceInfo.put("yield", context.get("yield"));
        			} else if("WL".equals(docClass)) {


        				referenceInfo.put("wlDocFileYN", context.get("wlDocFileYN"));
        				referenceInfo.put("wlDocFilePath", pdfUrl);
        			} else if("MTC".equals(docClass)) {


        				referenceInfo.put("mtcDocFileYN", context.get("mtcDocFileYN"));
        				referenceInfo.put("mtcDocFilePath", pdfUrl);
        				referenceInfo.put("mtcVerified", context.get("mtcVerified"));
        			} else if("SA".equals(docClass)) {


        				referenceInfo.put("shipmentAdviceDocFileYN", context.get("shipmentAdviceDocFileYN"));
        				referenceInfo.put("shipmentAdviceDocFilePath", pdfUrl);
        				referenceInfo.put("shippingAgent", context.get("shippingAgent"));
        				referenceInfo.put("email", context.get("email"));
        			}
        			Debug.logInfo("############################## = " + referenceInfo.toString(), null);
        			referenceInfo = delegator.createOrStore(referenceInfo);
        		}
        	}
        } catch (GenericEntityException e){
            Debug.logError(e, "Cannot createNupdateShippingDoc ", module);
        }

        result.put("poNo", poNo);
        result.put("lotNo", lotNo);

        return result;
    }

    public static File checkExistsPdf(File file) {
        if (!file.exists()) {
            pdfCount = 0;
            pdfPath = null;
            return file;
        }
        pdfCount++;
        String filePath = pdfPath.substring(0, pdfPath.lastIndexOf("."));
        String type = pdfPath.substring(pdfPath.lastIndexOf(".") + 1);
        file = new File(filePath + "(" + pdfCount + ")." + type);
        return checkExistsPdf(file);
    }
}