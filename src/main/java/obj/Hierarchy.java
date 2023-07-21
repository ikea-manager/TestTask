package obj;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public record Hierarchy(long objectId, long parentObjectId) {

    public static Map<Long, List<Hierarchy>> fromDocument(Document document) {
        var itemElements = document
                .getDocumentElement()
                .getElementsByTagName("ITEM");
        var items = new HashMap<Long, List<Hierarchy>>(itemElements.getLength());

        for (var i = itemElements.getLength() - 1; i > 0; i--) {
            var item = (Element) itemElements.item(i);

            var objectId = Long.parseLong(item.getAttribute("OBJECTID"));
            items.putIfAbsent(objectId, new LinkedList<>());
            items.get(objectId).add(new Hierarchy(
                    objectId,
                    Long.parseLong(item.getAttribute("PARENTOBJID"))
            ));
        }

        return items;
    }
}
