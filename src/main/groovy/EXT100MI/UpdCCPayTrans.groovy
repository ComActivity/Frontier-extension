/*
 ***************************************************************
 *                                                             *
 *                           NOTICE                            *
 *                                                             *
 *   THIS SOFTWARE IS THE PROPERTY OF AND CONTAINS             *
 *   CONFIDENTIAL INFORMATION OF INFOR AND/OR ITS AFFILIATES   *
 *   OR SUBSIDIARIES AND SHALL NOT BE DISCLOSED WITHOUT PRIOR  *
 *   WRITTEN PERMISSION. LICENSED CUSTOMERS MAY COPY AND       *
 *   ADAPT THIS SOFTWARE FOR THEIR OWN USE IN ACCORDANCE WITH  *
 *   THE TERMS OF THEIR SOFTWARE LICENSE AGREEMENT.            *
 *   ALL OTHER RIGHTS RESERVED.                                *
 *                                                             *
 *   (c) COPYRIGHT 2020 INFOR.  ALL RIGHTS RESERVED.           *
 *   THE WORD AND DESIGN MARKS SET FORTH HEREIN ARE            *
 *   TRADEMARKS AND/OR REGISTERED TRADEMARKS OF INFOR          *
 *   AND/OR ITS AFFILIATES AND SUBSIDIARIES. ALL RIGHTS        *
 *   RESERVED.  ALL OTHER TRADEMARKS LISTED HEREIN ARE         *
 *   THE PROPERTY OF THEIR RESPECTIVE OWNERS.                  *
 *                                                             *
 ***************************************************************
 */
 
 import groovy.lang.Closure
 
 import java.time.LocalDate;
 import java.time.LocalDateTime;
 import java.time.format.DateTimeFormatter;
 import java.time.ZoneId;
 import java.math.BigDecimal;
 import java.math.RoundingMode;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 
 
 /*
 *Modification area - M3
 *Nbr               Date      User id     Description
 *XXXX              20220510  XWZHAO      Credit card payment transactions
 *
 */
 
/*
* - Update payment transaction record to EXTCRD
*/
public class UpdCCPayTrans extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;
  private final MICallerAPI miCaller;
  private final LoggerAPI logger;
  private final ProgramAPI program;
  
  private String cono;
  private String divi;
  private String yea4;
  private String ivno;
  private String ivam;
  private String exid;
  private String prvp;
  private String prvi;
  private String tsta;
  private String autd;
  private String capd;
  private String setd;
  private String stam;
  private String atam;
  private String ogid;
  
  private int XXCONO;
  
  public UpdCCPayTrans(MIAPI mi, DatabaseAPI database, MICallerAPI miCaller, LoggerAPI logger, ProgramAPI program) {
    this.mi = mi;
    this.database = database;
    this.miCaller = miCaller;
    this.logger = logger;
    this.program = program;
  }
  
  public void main() {
    //Fetch input fields from MI
    cono = mi.inData.get("CONO") == null ? '' : mi.inData.get("CONO").trim();
  	if (cono == "?") {
  	  cono = "";
  	} 
    divi = mi.inData.get("DIVI") == null ? '' : mi.inData.get("DIVI").trim();
  	if (divi == "?") {
  	  divi = "";
  	} 
  	yea4 = mi.inData.get("YEA4") == null ? '' : mi.inData.get("YEA4").trim();
  	if (yea4 == "?") {
  	  yea4 = "";
  	} 
    ivno = mi.inData.get("IVNO") == null ? '' : mi.inData.get("IVNO").trim();
  	if (ivno == "?") {
  	  ivno = "";
  	}
  	ivam = mi.inData.get("IVAM") == null ? '' : mi.inData.get("IVAM").trim();
  	if (ivam == "?") {
  	  ivam = "";
  	}
  	exid = mi.inData.get("EXID") == null ? '' : mi.inData.get("EXID").trim();
  	if (exid == "?") {
  	  exid = "";
  	}
  	prvp = mi.inData.get("PRVP") == null ? '' : mi.inData.get("PRVP").trim();
  	if (prvp == "?") {
  	  prvp = "";
  	}
  	prvi = mi.inData.get("PRVI") == null ? '' : mi.inData.get("PRVI").trim();
  	if (prvi == "?") {
  	  prvi = "";
  	}
  	tsta = mi.inData.get("TSTA") == null ? '' : mi.inData.get("TSTA").trim();
  	if (tsta == "?") {
  	  tsta = "";
  	}
  	autd = mi.inData.get("AUTD") == null ? '' : mi.inData.get("AUTD").trim();
  	if (autd == "?") {
  	  autd = "";
  	}
  	capd = mi.inData.get("CAPD") == null ? '' : mi.inData.get("CAPD").trim();
  	if (capd == "?") {
  	  capd = "";
  	}
  	setd = mi.inData.get("SETD") == null ? '' : mi.inData.get("SETD").trim();
  	if (setd == "?") {
  	  setd = "";
  	}
  	stam = mi.inData.get("STAM") == null ? '' : mi.inData.get("STAM").trim();
  	if (stam == "?") {
  	  stam = "";
  	}
  	atam = mi.inData.get("ATAM") == null ? '' : mi.inData.get("ATAM").trim();
  	if (atam == "?") {
  	  atam = "";
  	}
  	ogid = mi.inData.get("OGID") == null ? '' : mi.inData.get("OGID").trim();
  	if (ogid == "?") {
  	  ogid = "";
  	}
  	
  	//Validate input fields
    if (!validateInput()) {
      return;
    }
    DBAction actionEXTCRD = database.table("EXTCRD").index("00").build();
    DBContainer EXTCRD = actionEXTCRD.getContainer();
    EXTCRD.set("EXCONO", XXCONO);
    EXTCRD.set("EXDIVI", divi);
    EXTCRD.set("EXEXID", exid);
    if (!actionEXTCRD.readLock(EXTCRD, updateEXTCRD)){
      mi.error("Record does not exists");
    }
  }
  /*
  * validateInput - Validate all the input fields - replicated from PECHK() of CRS575
  * @return false if there is any error
  *         true if pass the validation
  */
  boolean validateInput() { 
    
    if (!cono.isEmpty() ){
      if (cono.isInteger()){
        XXCONO= cono.toInteger();
      } else {
        mi.error("Company " + cono + " is invalid");
        return false;
      }
    } else {
      XXCONO= program.LDAZD.CONO;
    }
    if (divi.isEmpty()) {
      divi = program.LDAZD.DIVI;
    }
		if (exid.isEmpty()) {
      mi.error("External transaction ID cannot be blank.");
      return false;
    }
    if (!yea4.isEmpty() && !ivno.isEmpty()) {
      DBAction queryOINVOH = database.table("OINVOH").index("00").selection("UHIVNO", "UHIVAM").build();
      DBContainer OINVOH = queryOINVOH.getContainer();
  		OINVOH.set("UHCONO", XXCONO);
  		OINVOH.set("UHDIVI", divi);
  		OINVOH.set("UHYEA4", yea4.toInteger());
  		OINVOH.set("UHINPX", "");
  		OINVOH.set("UHIVNO", ivno.toInteger());
  		
  		if (!queryOINVOH.read(OINVOH)) {
  		  mi.error("Customer invoice " + ivno + " does not exist in OINVOH.");
        return false;
  		} 
    }
    if (!autd.isEmpty() && !isDateValid(autd)) {
      mi.error("Authorisaction date is not a valid date.");
      return false;
    }
    if (!capd.isEmpty() && !isDateValid(capd)) {
      mi.error("Capture date is not a valid date.");
      return false;
    }
    if (!setd.isEmpty() && !isDateValid(setd)) {
      mi.error("Settlement date is not a valid date");
      return
    }
    return true;
  }
  /**
   * isDateValid - check if input string is a valid date
   *  - date format: yyyyMMdd
   * return boolean
   */
  def isDateValid(String dateStr) {
    boolean dateIsValid = true;
    Matcher matcher=
      Pattern.compile("^((2000|2400|2800|(19|2[0-9](0[48]|[2468][048]|[13579][26])))0229)\$" 
        + "|^(((19|2[0-9])[0-9]{2})02(0[1-9]|1[0-9]|2[0-8]))\$"
        + "|^(((19|2[0-9])[0-9]{2})(0[13578]|10|12)(0[1-9]|[12][0-9]|3[01]))\$" 
        + "|^(((19|2[0-9])[0-9]{2})(0[469]|11)(0[1-9]|[12][0-9]|30))\$").matcher(dateStr)
    dateIsValid = matcher.matches()
    if (dateIsValid) {
      dateIsValid = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd")) != null
    } 
    return dateIsValid
  }
  /*
   * updateEXTCRD - Callback function to update EXTCRD table
   *
   */
  Closure updateEXTCRD = { LockedResult EXTCRD ->
    int currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toInteger();
    if (!yea4.isEmpty()) {
      EXTCRD.set("EXYEA4", yea4.toInteger());
    }
    if (!ivno.isEmpty()) {
      EXTCRD.set("EXIVNO", ivno.toInteger());
    }
    if (!ivam.isEmpty()) {
      EXTCRD.set("EXIVAM", ivam.toDouble());
    }
    if (!prvp.isEmpty()) {
      EXTCRD.set("EX3RDP", prvp);
    }
    if (!prvi.isEmpty()) {
  	  EXTCRD.set("EX3RDI", prvi);
    }
    if (!tsta.isEmpty()) {
  	  EXTCRD.set("EXTSTA", tsta);
    }
  	if (!autd.isEmpty()) {
  	  EXTCRD.set("EXAUTD", autd.toInteger());
  	}
  	if (!capd.isEmpty()) {
  	  EXTCRD.set("EXCAPD", capd.toInteger());
  	}
  	if (!setd.isEmpty()) {
  	  EXTCRD.set("EXSETD", setd.toInteger());
  	}
  	if (!stam.isEmpty()) {
  	  EXTCRD.set("EXSTAM", stam.toDouble());
  	}
  	if (!atam.isEmpty()) {
  	  EXTCRD.set("EXATAM", atam.toDouble());
  	}
  	if (!ogid.isEmpty()) {
  	  EXTCRD.set("EXOGID", ogid);
  	}
  	EXTCRD.set("EXLMDT", currentDate);
  	EXTCRD.set("EXCHNO", EXTCRD.get("EXCHNO").toString().toInteger() +1);
  	EXTCRD.set("EXCHID", program.getUser());
    EXTCRD.update();
   
  }
}
