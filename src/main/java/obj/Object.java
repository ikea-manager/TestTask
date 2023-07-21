package obj;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.time.LocalDate;
import java.util.*;

public record Object(long objectId, String name, String typeName, LocalDate startDate, LocalDate endDate) {

    @Override
    public String toString() {
        return String.format("%s %s", typeName, name);
    }

    public static Map<Long, List<Object>> fromDocument(Document document) {
        var objectElements = document
                .getDocumentElement()
                .getElementsByTagName("OBJECT");
        var objects = new LinkedHashMap<Long, List<Object>>(objectElements.getLength());

        for (var i = objectElements.getLength() - 1; i > 0; i--) {
            var object = (Element) objectElements.item(i);

            var objectId = Long.parseLong(object.getAttribute("OBJECTID"));
            objects.putIfAbsent(objectId, new LinkedList<>());
            objects.get(objectId).add(new Object(
                    objectId,
                    object.getAttribute("NAME"),
                    object.getAttribute("TYPENAME"),
                    LocalDate.parse(object.getAttribute("STARTDATE")),
                    LocalDate.parse(object.getAttribute("ENDDATE"))
            ));
        }

        return objects;
    }
}
