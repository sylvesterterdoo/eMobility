import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class ChargingStationTest {


    @Test
    public void customerCheckStationIsOpenWhenStationIsClosed() {
        LocalDateTime time = LocalDateTime.of(2020, 12, 15, 12, 10, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));
        boolean result = getStoreChargingStation(tenant, StationType.CUSTOMER).
                isChargingStationOpenDuring(Timestamp.valueOf(time));
        Assert.assertFalse(result);
    }

    @Test
    public void customerCheckStationIsOpenWhenStationIsOpen() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 12, 30, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));
        boolean result = getStoreChargingStation(tenant, StationType.CUSTOMER).
                isChargingStationOpenDuring(Timestamp.valueOf(time));
        Assert.assertTrue(result);
    }


    @org.junit.Test
    public void employeeCheckStationIsOpenWhenStationIsOpen1() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 15, 12, 10, 20);
        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));
        boolean result = getStoreChargingStation(tenant, StationType.EMPLOYEE).
                isChargingStationOpenDuring(Timestamp.valueOf(time));

        Assert.assertTrue(result);
    }

    @Test
    public void employeeCheckStationIsOpenWhenStationIsClosed2() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 16, 30, 20);
        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));
        boolean result = getStoreChargingStation(tenant, StationType.EMPLOYEE).
                isChargingStationOpenDuring(Timestamp.valueOf(time));

        Assert.assertFalse(result);
    }

    @Test
    public void employeeCheckStationIsOpenWhenStationIsOpen() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 6, 30, 20);
        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));
        boolean result = getStoreChargingStation(tenant, StationType.EMPLOYEE).
                isChargingStationOpenDuring(Timestamp.valueOf(time));

        Assert.assertTrue(result);
    }

    @Test
    public void managerCheckStationIsOpenWhenStationIsOpen() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 23, 30, 20);
        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));
        boolean result = getStoreChargingStation(tenant, StationType.MANAGER).
                isChargingStationOpenDuring(Timestamp.valueOf(time));

        Assert.assertTrue(result);
    }

    @Test
    public void customerCheckStationIsOpenWhenStationIsClosedOpenException() {
        LocalDateTime time = LocalDateTime.of(2020, 12, 15, 12, 10, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));

        LocalDate from = LocalDate.of(2020, 12, 15);
        LocalDate to = LocalDate.of(2020, 12, 15);
        TimeSpan timeSpan = new TimeSpan(LocalTime.of(12, 00), LocalTime.of(16, 00));

        Exception chargingStationException = new Exception(from, to, timeSpan, ExceptionType.OPEN);

        ChargingStation chargingStation = getStoreChargingStation(tenant, StationType.CUSTOMER);
        chargingStation.addException(chargingStationException);

        boolean result = chargingStation.isChargingStationOpenDuring(Timestamp.valueOf(time));

        Assert.assertTrue(result);
    }

    @Test
    public void customerCheckStationIsOpenWhenStationIsOpenClosedException() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 12, 30, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));

        LocalDate from = LocalDate.of(2020, 12, 23);
        LocalDate to = LocalDate.of(2020, 12, 23);
        TimeSpan timeSpan = new TimeSpan(LocalTime.of(9, 00), LocalTime.of(16, 00));

        Exception chargingStationException = new Exception(from, to, timeSpan, ExceptionType.CLOSE);

        ChargingStation chargingStation = getStoreChargingStation(tenant, StationType.CUSTOMER);
        chargingStation.addException(chargingStationException);

        boolean result = chargingStation.isChargingStationOpenDuring(Timestamp.valueOf(time));
        Assert.assertFalse(result);
    }

    @Test
    public void employeeCheckStationStoreClosedExceptionWithinASpecifiedRange() {

        LocalDateTime time = LocalDateTime.of(2020, 6, 3, 12, 30, 20);

        LocalDate from = LocalDate.of(2020, 6, 1);
        LocalDate to = LocalDate.of(2020, 6, 5);
        TimeSpan timeSpan = new TimeSpan(LocalTime.of(6, 00), LocalTime.of(18, 00));

        Exception storeException = new Exception(from, to, timeSpan, ExceptionType.CLOSE);

        Tenant tenant = new Tenant("eMobility");
        Store store = new Store(tenant);
        store.addException(storeException);
        tenant.addStore(store);

        ChargingStation chargingStation = getStoreChargingStation(tenant, StationType.CUSTOMER);

        boolean result = chargingStation.isChargingStationOpenDuring(Timestamp.valueOf(time));
        Assert.assertFalse(result);
    }

    @Test
    public void employeeCheckStationTenantClosedExceptionWithinASpecifiedRange() {
        // double check.
        LocalDateTime time = LocalDateTime.of(2020, 5, 2, 1, 30, 20);

        LocalDate from = LocalDate.of(2020, 5, 1);
        LocalDate to = LocalDate.of(2020, 5, 2);
        TimeSpan timeSpan = new TimeSpan(LocalTime.of(0, 00), LocalTime.of(0, 00));

        Exception tenantException = new Exception(from, to, timeSpan, ExceptionType.CLOSE);

        Tenant tenant = new Tenant("eMobility");
        tenant.addException(tenantException);
        tenant.addStore(new Store(tenant));

        ChargingStation chargingStation = getStoreChargingStation(tenant, StationType.EMPLOYEE);

        boolean result = chargingStation.isChargingStationOpenDuring(Timestamp.valueOf(time));
        Assert.assertFalse(result);
    }


    @Test
    public void customerChecksStationStatusNextOpeningTime() {

        // Tuesday 12:10:20
        LocalDateTime time = LocalDateTime.of(2020, 12, 22, 12, 10, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));
        Timestamp timestamp = getStoreChargingStation(tenant, StationType.CUSTOMER)
                .nextOpenClosedStatusChange(Timestamp.valueOf(time));

        LocalDateTime expected = LocalDateTime.of(2020, 12, 22, 13, 00, 00);

        Assert.assertEquals(expected, timestamp.toLocalDateTime());

    }


    @Test
    public void customerChecksStationStatusNextClosingTime() {

        // Wednesday 12:10:20
        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 12, 10, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));

        Timestamp timestamp = getStoreChargingStation(tenant, StationType.CUSTOMER)
                .nextOpenClosedStatusChange(Timestamp.valueOf(time));

        LocalDateTime expected = LocalDateTime.of(2020, 12, 23, 13, 00, 00);

        Assert.assertEquals(expected, timestamp.toLocalDateTime());

    }

    @Test
    public void customerChecksStationStatusNextClosingTimeSaturday() {

        // Saturday 15:30:20
        LocalDateTime time = LocalDateTime.of(2020, 12, 26, 15, 30, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));

        Timestamp timestamp = getStoreChargingStation(tenant, StationType.CUSTOMER)
                .nextOpenClosedStatusChange(Timestamp.valueOf(time));

        LocalDateTime expected = LocalDateTime.of(2020, 12, 28, 8, 00, 00);

        Assert.assertEquals(expected, timestamp.toLocalDateTime());

    }

    @org.junit.Test
    public void employeeChecksStationStatusNextOpeningTimeEmployee() {

        // Tuesday 12:10:20
        LocalDateTime time = LocalDateTime.of(2020, 12, 22, 12, 40, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));

        Timestamp timestamp = getStoreChargingStation(tenant, StationType.EMPLOYEE)
                .nextOpenClosedStatusChange(Timestamp.valueOf(time));

        LocalDateTime expected = LocalDateTime.of(2020, 12, 22, 19, 00, 00);

        Assert.assertEquals(expected, timestamp.toLocalDateTime());

    }


    @Test
    public void employeeChecksStationStatusNextClosingTimeEmployee() {

        // Wednesday 15:30:20
        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 15, 30, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));

        Timestamp timestamp = getStoreChargingStation(tenant, StationType.EMPLOYEE)
                .nextOpenClosedStatusChange(Timestamp.valueOf(time));

        LocalDateTime expected = LocalDateTime.of(2020, 12, 24, 6, 30, 00);

        Assert.assertEquals(expected, timestamp.toLocalDateTime());

    }


    @org.junit.Test
    public void employeeCheckStationStatusNextClosingTimeEmployeeSaturday() {

        // Saturday 15:30:20
        LocalDateTime time = LocalDateTime.of(2020, 12, 26, 15, 30, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));

        Timestamp timestamp = getStoreChargingStation(tenant, StationType.EMPLOYEE)
                .nextOpenClosedStatusChange(Timestamp.valueOf(time));

        LocalDateTime expected = LocalDateTime.of(2020, 12, 28, 6, 30, 00);

        Assert.assertEquals(expected, timestamp.toLocalDateTime());

    }

    @Test
    public void customerChecksStationStatusNextOpeningTimeOpenException() {
        // Tuesday 12:10:20
        LocalDateTime time = LocalDateTime.of(2020, 12, 15, 12, 10, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));

        LocalDate from = LocalDate.of(2020, 12, 15);
        LocalDate to = LocalDate.of(2020, 12, 15);
        TimeSpan timeSpan = new TimeSpan(LocalTime.of(12, 00), LocalTime.of(16, 00));

        Exception chargingStationException = new Exception(from, to, timeSpan, ExceptionType.OPEN);

        ChargingStation chargingStation = getStoreChargingStation(tenant, StationType.CUSTOMER);
        chargingStation.addException(chargingStationException);

        Timestamp timestamp = chargingStation.nextOpenClosedStatusChange(Timestamp.valueOf(time));

        LocalDateTime expected = LocalDateTime.of(2020, 12, 15, 16, 00, 00);

        Assert.assertEquals(expected, timestamp.toLocalDateTime());
    } 

    @Test
    public void customerChecksStationStatusNextOpeningTimeClosedException() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 12, 30, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store(tenant));

        LocalDate from = LocalDate.of(2020, 12, 23);
        LocalDate to = LocalDate.of(2020, 12, 23);
        TimeSpan timeSpan = new TimeSpan(LocalTime.of(9, 00), LocalTime.of(16, 00));

        Exception chargingStationException = new Exception(from, to, timeSpan, ExceptionType.CLOSE);

        ChargingStation chargingStation = getStoreChargingStation(tenant, StationType.CUSTOMER);
        chargingStation.addException(chargingStationException);

        Timestamp timestamp = chargingStation.nextOpenClosedStatusChange(Timestamp.valueOf(time));

        LocalDateTime expected = LocalDateTime.of(2020, 12, 23, 16, 00, 00);

        Assert.assertEquals(expected, timestamp.toLocalDateTime());
    }

    @Test
    public void customerChecksStationStatusNextOpeningTimeClosedExceptionWithinASpecifiedRange() {

        LocalDateTime time = LocalDateTime.of(2020, 6, 3, 12, 30, 20);

        LocalDate from = LocalDate.of(2020, 6, 1);
        LocalDate to = LocalDate.of(2020, 6, 5);
        TimeSpan timeSpan = new TimeSpan(LocalTime.of(6, 00), LocalTime.of(18, 00));

        Exception storeException = new Exception(from, to, timeSpan, ExceptionType.CLOSE);

        Tenant tenant = new Tenant("eMobility");
        Store store = new Store(tenant);
        store.addException(storeException);
        tenant.addStore(store);

        ChargingStation chargingStation = getStoreChargingStation(tenant, StationType.EMPLOYEE);

        Timestamp timestamp = chargingStation.nextOpenClosedStatusChange(Timestamp.valueOf(time));

        LocalDateTime expected = LocalDateTime.of(2020, 6, 5, 18, 00, 00);

        Assert.assertEquals(expected, timestamp.toLocalDateTime());
    }



    private static ChargingStation getStoreChargingStation(Tenant tenant, StationType stationType) {
        return new ChargingStation(tenant.stores.get(0), stationType);
    }

}