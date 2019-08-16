package yaycrawler.common.utils;

/**
 * Created by bill on 2017/5/3.
 */

import org.apache.commons.net.ftp.FTPFile;

import org.apache.commons.net.ftp.FTPFileFilter;

/**
 * 获取文件时过滤文件后缀的过滤器
 *
 * @author zzj
 * @date Jun 7, 2016 3:37:36 PM
 */
public class FTPFileSuffixFilter implements FTPFileFilter {

    /**
     * 传过来的后缀信息(多个时用英文逗号隔开)
     */
    private String fileSuffix;

    /**
     * 初始化参数
     *
     * @param subffix 后缀
     */
    public FTPFileSuffixFilter(String subffix) {
        this.fileSuffix = subffix;
    }

    /* (non-Javadoc)
     * @see org.apache.commons.net.ftp.FTPFileFilter#accept(org.apache.commons.net.ftp.FTPFile)
     */
    @Override
    public boolean accept(FTPFile file) {
        String filename = file.getName();
        String[] strings = fileSuffix.split(",");
        boolean flag = false;
        for (String suf : strings) {
            if (filename.lastIndexOf(suf) != -1) {
                flag = true;
            }
        }
        return flag;
    }

}

