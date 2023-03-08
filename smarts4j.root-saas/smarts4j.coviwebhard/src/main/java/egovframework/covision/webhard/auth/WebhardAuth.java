package egovframework.covision.webhard.auth;

import egovframework.baseframework.base.StaticContextAccessor;
import egovframework.baseframework.data.CoviMap;
import egovframework.baseframework.util.SessionHelper;
import egovframework.baseframework.util.StringUtil;
import egovframework.covision.webhard.common.service.WebhardFolderSvc;

public class WebhardAuth {
	
	/**
	 * 소유자 권한
	 * @param CoviMap params
	 * @return boolean
	 */
	public static boolean getOwnerAuth(CoviMap params){
		boolean bOwnerAuth = false;
		
		try {
			WebhardFolderSvc webhardFolderSvc = StaticContextAccessor.getBean(WebhardFolderSvc.class);
			
			// 사용자 드라이브 접근 체크
			if (params.getString("folderType").equals("Normal") && StringUtil.isNotNull(params.getString("folderID"))) { // 내 드라이브 하위 폴더
				CoviMap fParams = new CoviMap();
				fParams.put("folderUuid", params.getString("folderID"));
				
				CoviMap folderInfo = webhardFolderSvc.getFolderInfo(fParams);
				
				if (folderInfo != null && folderInfo.size() != 0) {
					if (folderInfo.getString("ownerType").equalsIgnoreCase("U")
						&& folderInfo.getString("ownerId").equals(SessionHelper.getSession("USERID"))) {
						bOwnerAuth = true;
					} else if (folderInfo.getString("ownerType").equalsIgnoreCase("G")
						&& SessionHelper.getSession("GR_GroupPath").indexOf(folderInfo.getString("ownerId") + ";") > -1) {
						bOwnerAuth = true;
					}
				}
			} else { // 내 드라이브, 공유 받은 폴더, 공유한 폴더, 최근 문서함, 휴지통
				bOwnerAuth = true;
			}
		} catch(NullPointerException e) {
			return false;
		} catch(Exception e) {
			return false;
		}
		
		return bOwnerAuth;
	}
	
}
