package org.fao.gift.utils;

import org.fao.fenix.commons.msd.dto.full.Period;
import org.fao.fenix.commons.msd.dto.type.DataType;

import javax.ws.rs.NotSupportedException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

}
