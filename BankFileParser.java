public class BankFileParser extends AbsFileParser {

    /**
     * logger
     */
    private static final ZSmartLogger logger = ZSmartLogger.getLogger(BankFileParser.class);
    // 文件头4个参数
    private static final int numFileHeader = 4;
    // 文件体每行都是6个参数
    private static final int numFileBody = 6;


    @Override
    protected LineContentDto parseLineContent(String lineContent, int lineNo) throws BaseAppException {
        logger.debug("BankFileParser[parseLineContent] begin, lineNo = [{}], lineContent = [{}]", lineNo, lineContent);
        if (StringUtils.isEmpty(lineContent)) {
            return null;
        }

        LineContentDto lineContentDto;
        if (lineNo == 1) {
            lineContentDto = parseFileHead(lineContent);
        }
        else {
            lineContentDto = parseFileBody(lineContent);
        }

        logger.debug("BankFileParser[parseLineContent] end, lineContentDto = [{}]", lineContentDto);
        return lineContentDto;
    }

    /**
     * 解析文件头
     *
     * @param lineContent 一行内容 <br>
     * @return LineContentDto <br>
     * @author xu.chao01 <br>
     * @taskId <br>
     */
    private LineContentDto parseFileHead(String lineContent) throws BaseAppException {
        LineContentDto lineContentDto = new LineContentDto();
        String[] lineArray = lineContent.split("\\|");

        if (lineArray.length != numFileHeader) {
            // 文件头不是numFileHeader个参数的，结束解析
            return null;
        }

        // 判断是否有空字符串
        for (String str : lineArray) {
            if (StringUtils.isEmpty(str)) {
                logger.error("lineContent [{}] is incorrect.", lineContent);
                return null;
            }
        }

        try {
            lineContentDto.setTotalAmount(Long.valueOf(lineArray[0]));
            lineContentDto.setTotalCount(Long.valueOf(lineArray[1]));
        }
        catch (java.lang.NumberFormatException e) {
            logger.error("totalAmount [{}] or totalCount [{}] is incorrect.", lineArray[0], lineArray[1]);
            return null;
        }


        lineContentDto.setBeginSerialId(lineArray[2]);
        lineContentDto.setEndSerialId(lineArray[3]);

        logger.debug("File head, totalAmout = [{}], totalCount = [{}], beginSerialId = [{}], endSerialId = [{}]",
                lineArray[0], lineArray[1], lineArray[2], lineArray[3]);

        return lineContentDto;
    }

    /**
     * 解析文件体
     *
     * @param lineContent <br>
     * @return LineContentDto <br>
     * @throws BaseAppException <br>
     * @author xu.chao01 <br>
     * @taskId <br>
     */
    private LineContentDto parseFileBody(String lineContent) throws BaseAppException {
        LineContentDto lineContentDto = new LineContentDto();
        String[] lineArray = lineContent.split("\\|");

        if (lineArray.length != numFileBody) {
            // 文件体每行不是numFileBody个参数，结束解析
            return null;
        }

        // 判断是否有空字符串
        for (String str : lineArray) {
            if (StringUtils.isEmpty(str)) {
                logger.error("lineContent [{}] is incorrect.", lineContent);
                return null;
            }
        }

        // 文件体中没有文件头字段，置零或空
        lineContentDto.setTotalAmount(0L);
        lineContentDto.setTotalCount(0L);
        lineContentDto.setBeginSerialId("");
        lineContentDto.setEndSerialId("");
        PartnerReconcileDto prDto = new PartnerReconcileDto();
        lineContentDto.setPartnerReconcileDto(prDto);

        prDto.setReqSerial(lineArray[0]);
        prDto.setDestinationType(lineArray[1]);
        prDto.setDestinationId(lineArray[2]);
        prDto.setDestinationAttr(lineArray[3]);
        prDto.setOperType(Long.valueOf(lineArray[5]));

        try {
            prDto.setRechargeBalance(Long.valueOf(lineArray[4]));
        }
        catch (java.lang.NumberFormatException e) {
            logger.error("rechargeBalance [{}] is incorrect.", lineArray[4]);
            return null;
        }

        logger.debug("File head, reqSerial = [{}], destinationType = [{}], destinationId = [{}], " +
                        "destinationAttr = [{}], rechargeBalance = [{}], operType = [{}]",
                lineArray[0], lineArray[1], lineArray[2], lineArray[3], lineArray[4], lineArray[5]);

        return lineContentDto;
    }
}
