package json

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DatabindContext
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver
import com.fasterxml.jackson.databind.type.TypeFactory
import org.reflections.Reflections


interface Event {
    val source: String
}

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")

@JsonSubTypes(
        JsonSubTypes.Type(value = UserCreatedEvent::class, name = "userCreated"),
        JsonSubTypes.Type(value = UserActiveEvent::class, name = "userActive"))

abstract class EventBase: Event

data class UserCreatedEvent(override val source: String): EventBase()

data class UserActiveEvent(override val source: String, val action: String) : EventBase()


@JsonTypeInfo(
        use = JsonTypeInfo.Id.CUSTOM,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type")
@JsonTypeIdResolver(EventTypeIdResolver::class)
abstract class CustomEventBase: Event

data class Event1(override val source: String) : CustomEventBase()

class EventTypeIdResolver: TypeIdResolver {



    override fun idFromValue(value: Any): String {
        return value.javaClass.simpleName.decapitalize()
    }

    override fun getDescForKnownTypeIds(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun idFromBaseType(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun idFromValueAndType(value: Any?, suggestedType: Class<*>?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMechanism(): JsonTypeInfo.Id {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun init(baseType: JavaType) {
        println(baseType.rawClass)

        val reflections = Reflections("json")

        val subTypes = reflections.getSubTypesOf(baseType.rawClass)

        println(subTypes)
    }

    override fun typeFromId(context: DatabindContext, id: String): JavaType {
        val simpleName = id.capitalize()

        val packageName = EventBase::class.java.`package`.name

        val clazzName = "$packageName.$simpleName"

        println(clazzName)

        return TypeFactory.defaultInstance().constructFromCanonical(clazzName)
    }
}