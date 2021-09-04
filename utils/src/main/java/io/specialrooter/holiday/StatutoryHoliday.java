package io.specialrooter.holiday;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 法定节假日
 */
public interface StatutoryHoliday {

    List<Holiday> holidays();
    Set<LocalDate> holidaySet();
}
