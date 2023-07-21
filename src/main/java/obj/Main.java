package obj;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Main {

    private static final Document objectsDocument = getDocument("AS_ADDR_OBJ.XML");
    private static final Document hierarchyDocument = getDocument("AS_ADM_HIERARCHY.XML");

    private static final Map<Long, List<Object>> objects = Object.fromDocument(objectsDocument);
    private static final Map<Long, List<Hierarchy>> hierarchies = Hierarchy.fromDocument(hierarchyDocument);

    public static void main(String[] args) {

        printObjects();

        printChain("проезд");
    }

    private static void printObjects() {
        try (var scanner = new Scanner(System.in)) {
            System.out.print("Введите дату (в формате гггг-ММ-дд): ");
            var date = LocalDate.parse(scanner.nextLine());

            System.out.print("Введите идентификатор объектов (через запятую): ");
            var objectIds = Arrays.stream(scanner.nextLine().split(","))
                    .mapToLong(Long::parseLong)
                    .toArray();

            printObjects(date, objectIds);
        }
    }

    private static void printObjects(LocalDate date, long... objectIds) {
        LongStream.of(objectIds)
                .mapToObj(objects::get)
                .map(objects -> objects.stream()
                        .filter(object -> object.endDate().isAfter(date) && object.startDate().isBefore(date))
                        .findFirst())
                .map(optionalObject -> optionalObject.map(object -> String.format("%s: %s", object.objectId(), object)).orElse(null))
                .forEach(System.out::println);
    }

    private static void printChain(String typeName) {
        objects.values().stream()
                .map(objects -> objects.get(0))
                .filter(object -> object.typeName().equals(typeName))
                .map(Main::findHierarchy)
                .map(hierarchy -> hierarchy.stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")))
                .forEach(System.out::println);
    }

    private static List<Object> findHierarchy(Object object) {
        var hierarchy = new LinkedList<Object>();

        while (true) {
            hierarchy.addFirst(object);

            var parents = hierarchies.get(object.objectId());
            if (parents == null)
                break;

            object = objects
                    .get(parents.get(0).parentObjectId())
                    .get(0);
        }

        return hierarchy;
    }

    private static Document getDocument(String name) {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(Main.class.getClassLoader().getResourceAsStream(name));
        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
