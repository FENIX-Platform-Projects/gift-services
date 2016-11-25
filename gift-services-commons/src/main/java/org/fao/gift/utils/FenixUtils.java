package org.fao.gift.utils;

import org.fao.fenix.commons.find.dto.filter.CodesFilter;
import org.fao.fenix.commons.find.dto.filter.TimeFilter;
import org.fao.fenix.commons.msd.dto.full.OjCode;
import org.fao.fenix.commons.msd.dto.full.OjCodeList;
import org.fao.fenix.commons.msd.dto.full.Period;
import org.fao.fenix.commons.msd.dto.type.DataType;

import javax.ws.rs.NotSupportedException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class FenixUtils {


    public Period toPeriod(Date from, Date to, DataType type) throws NotSupportedException {
        return new Period(toFenixDate(from, type), toFenixDate(to, type));
    }

    private static final SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    private static final SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
    private static final SimpleDateFormat dayFormat = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMddhhmmss");
    public Long toFenixDate(Date date, DataType type) throws NotSupportedException {
        if (date == null)
            return null;

        SimpleDateFormat format = null;
        if (type!=null)
            switch (type) {
                case year: format = yearFormat; break;
                case month: format = monthFormat; break;
                case date: format = dayFormat; break;
                case time: format = timeFormat; break;
            }
            if (format==null)
                throw new NotSupportedException();
        return new Long(format.format(date));
    }


    public OjCodeList toOjCodeList(String uid, String version, Collection<String> codes) {
        if (codes==null || uid==null || codes.size()==0)
            return null;

        OjCodeList ojCodeList = new OjCodeList();
        ojCodeList.setIdCodeList(uid);
        ojCodeList.setVersion(version);

        Collection<OjCode> ojCodes = new LinkedList<>();
        ojCodeList.setCodes(ojCodes);

        for (String code : codes) {
            OjCode ojCode = new OjCode();
            ojCode.setCode(code);
        }

        return ojCodeList;
    }

    public CodesFilter toCodesFilter(String uid, String version, Collection<String> codes) {
        if (codes==null || uid==null || codes.size()==0)
            return null;

        CodesFilter codesFilter = new CodesFilter();
        codesFilter.uid = uid;
        codesFilter.version = version;
        codesFilter.codes = codes;
        return codesFilter;

    }

    public TimeFilter toTimeFilter (Long from, Long to, DataType type) {
        if ((from==null & to==null) | type==null)
            return null;

        TimeFilter timeFilter = new TimeFilter();
        switch (type) {
            case year:
                from = from!=null ? from*100+1 : null;
                to = to!=null ? to*100+12 : null;
            case month:
                from = from!=null ? from*100+1 : null;
                to = to!=null ? to*100+31 : null; //TODO use the right day number
            case date:
                from = from!=null ? from*1000000 : null;
                to = to!=null ? to*1000000+235959 : null;
        }
        timeFilter.from = from;
        timeFilter.to = to;

        return timeFilter;
    }
}
