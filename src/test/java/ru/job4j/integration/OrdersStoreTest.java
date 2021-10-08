package ru.job4j.integration;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class OrdersStoreTest {
    private static BasicDataSource pool = new BasicDataSource();

    private static String sqlCreateScript;

    private static String sqlDropScript;

    @BeforeClass
    public static void setUp() throws SQLException {
        pool.setDriverClassName("org.hsqldb.jdbcDriver");
        pool.setUrl("jdbc:hsqldb:mem:tests;sql.syntax_pgs=true");
        pool.setUsername("sa");
        pool.setPassword("");
        pool.setMaxTotal(14);
        sqlCreateScript = readScriptFile("./db/update_001.sql");
        sqlDropScript = readScriptFile("./db/drop_001.sql");
    }

    private static String readScriptFile(String fileName) {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName)))
        ) {
            br.lines().forEach(line -> builder.append(line).append(System.lineSeparator()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    @Before
    public void createTables() throws SQLException {
        pool.getConnection().prepareStatement(sqlCreateScript).executeUpdate();
    }

    @After
    public void wipeTables() throws SQLException {
        pool.getConnection().prepareStatement(sqlDropScript).executeUpdate();
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        pool.close();
    }

    @Test
    public void whenSaveOrderAndFindAllOneRowWithDescription() {
        OrdersStore store = new OrdersStore(pool);

        store.save(Order.of("name1", "description1"));

        List<Order> all = (List<Order>) store.findAll();

        assertThat(all.size(), is(1));
        assertThat(all.get(0).getDescription(), is("description1"));
        assertThat(all.get(0).getId(), is(1));
    }

    @Test
    public void whenSave3OrdersThenFindAllWithAllFields() {
        OrdersStore store = new OrdersStore(pool);
        Order order1 = Order.of("name1", "description1");
        Order order2 = Order.of("name2", "description2");
        Order order3 = Order.of("name3", "description3");
        store.save(order1);
        store.save(order2);
        store.save(order3);
        List<Order> exceptedList = List.of(order1, order2, order3);
        List<Order> resultList = (List<Order>) store.findAll();

        assertThat(resultList.size(), is(exceptedList.size()));

        Order resultOrder1 = resultList.get(0);
        Order exceptedOrder1 = exceptedList.get(0);
        compareTwoOrders(resultOrder1, exceptedOrder1);

        Order resultOrder2 = resultList.get(1);
        Order exceptedOrder2 = exceptedList.get(1);
        compareTwoOrders(resultOrder2, exceptedOrder2);

        Order resultOrder3 = resultList.get(2);
        Order exceptedOrder3 = exceptedList.get(2);
        compareTwoOrders(resultOrder3, exceptedOrder3);
    }

    @Test
    public void whenTableOrdersEmptyThenFindAll() {
        OrdersStore store = new OrdersStore(pool);
        List<Order> resultList = (List<Order>) store.findAll();
        assertThat(resultList.size(), is(0));
    }

    @Test
    public void whenSaveOrderThenFindById() {
        OrdersStore store = new OrdersStore(pool);
        Order expected = Order.of("test", "some text");
        int id = store.save(expected).getId();
        Order result = store.findById(id);
        assertThat(result.getName(), is(expected.getName()));
        assertThat(result.getDescription(), is(expected.getDescription()));
        assertThat(result.getCreated(), is(expected.getCreated()));
    }

    @Test
    public void whenSaveOrderAndUpdateOrderThenFindById() {
        OrdersStore store = new OrdersStore(pool);
        Order order = Order.of("test", "some text");
        int id = store.save(order).getId();
        Order expected = Order.of("new test", "new some text");
        expected.setId(id);
        assertThat(store.update(expected), is(true));
        Order result = store.findById(id);
        assertThat(result.getName(), is(expected.getName()));
        assertThat(result.getDescription(), is(expected.getDescription()));
        assertThat(result.getCreated(), is(expected.getCreated()));
    }

    @Test
    public void whenTryUpdateButThereIsNoOrder() {
        OrdersStore store = new OrdersStore(pool);
        Order order = Order.of("test", "some text");
        order.setId(1);
        assertThat(store.update(order), is(false));
    }

    @Test
    public void whenSave3OrdersIncluding2WithSameNameThenFindByName() {
        OrdersStore store = new OrdersStore(pool);
        Order order1 = Order.of("test1", "desc1");
        Order order2 = Order.of("test1", "desc2");
        Order order3 = Order.of("test3", "desc3");
        store.save(order1);
        store.save(order2);
        store.save(order3);
        List<Order> exceptedList = List.of(order1, order2);
        List<Order> resultList = (List<Order>) store.findByName(order1.getName());

        assertThat(resultList.size(), is(exceptedList.size()));

        Order resultOrder1 = resultList.get(0);
        Order exceptedOrder1 = exceptedList.get(0);
        compareTwoOrders(resultOrder1, exceptedOrder1);

        Order resultOrder2 = resultList.get(1);
        Order exceptedOrder2 = exceptedList.get(1);
        compareTwoOrders(resultOrder2, exceptedOrder2);
    }

    private void compareTwoOrders(Order resultOrder, Order exceptedOrder) {
        assertThat(resultOrder.getId(), is(exceptedOrder.getId()));
        assertThat(resultOrder.getName(), is(exceptedOrder.getName()));
        assertThat(resultOrder.getDescription(), is(exceptedOrder.getDescription()));
        assertThat(resultOrder.getCreated(), is(exceptedOrder.getCreated()));
    }
}