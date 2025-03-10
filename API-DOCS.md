# API Documentation

## Introduction

Welcome to the **OriginsPaper** API documentation. This document provides instructions on how to interact with
OriginsPaper's
codebase for external plugin use, or contributing.

## Adding the API to Gradle Kotlin Script

Just add the repository where the API is hosted to your `build.gradle.kts` file and the dependency. Ensure you have
paperweight installed aswell, because OriginsPaper uses NMS and CraftBukkit instances throughout the plugin and API

```kotlin
repositories {
    mavenCentral()
    maven {
        url = uri("https://jitpack.io/")
    }
}

dependencies {
    implementation("io.github.Dueris:OriginsPaper:mc1.21~0-v1.3.1")
}
```

## Setting up OriginsPaper hooking

Setting up OriginsPaper to hook into it is quite simple, and doesnt require much work at all.

OriginsPaper starts the Calio parser during its init process, meaning you need to add it to your dependencies list
inside
your `paper-plugin.yml` file. This guide will go over how to create new PowerTypes, Conditions, Actions, and how to even
replace/remove instances of them within OriginsPaper.
What your depend list should look like:

```yaml
dependencies:
  server:
    OriginsPaper:
      load: AFTER
      required: true
```

### WARNING: You must have OriginsPaper load AFTER your plugin or else your PowerTypes and others will not be registered properly and Calio will not recognize your instances.

## Creating a new PowerType

The Power system in OriginsPaper works closely to the Apoli PowerType system, where each PowerType instance is a
representation of a Power json inside the datapack. For example:

I have a power in ``data/dueris/apoli/power/test.json`` with the `"type"` field as `"apoli:test"`, and a PowerType of
the
type `"apoli:test"`. Calio will create a new instance of the PowerType and register it using defined AccessorKeys(more
details bellow in the `Calio` section).

Lets start by creating a new class:

```java
public class ApoliExamplePower extends PowerType {

}
```

In Calio/Apoli, a PowerType is an object that defines the main logic and handling of powers for the players it has
assigned to it.
To create an instance of any class inside Calio during parsing, it reads the SerializableData, which is defined by the
method ``static SerializableData buildFactory()``. SerializableDatas are definition of a Constructor for the
FactoryHolder. The first instance
added is the first arg, the second being the second arg, etc. It also provides the key for the `"type"` field. However,
in Calio, there
is a required argument of a ResourceLocation at the beginning of the constructor your SerializableData is creating,
which is the `"key"` argument.
The key argument is the argument assigned to the instance upon instance creation, named after the Namesace and Key of
the json.
With adding the constructor, it becomes:

```java
public class ApoliExamplePower extends PowerType {
	public ApoliExamplePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}
}
```

Adding the method to create the SerializableData:

```java
public class ApoliExamplePower extends PowerType {
	public ApoliExamplePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	// buildFactory method. We call the PowerType.buildFactory method first to define those args
	// from the base instead of Copy/Pasting its buildFactory method and adding to it.
	// By default, the PowerType class does not contain a Namespace, which is why ofNamespace needs to be added.
	// The way this is made allows for creating easy extensions of any power without too much struggle.
	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(ResourceLocation.fromNamespaceAndPath("example", "apoli"));
	}
}
```

To Add your own fields to the power json, its as simple as defining what field name, SerializableDataType,
which is an instance of a deserializer for json elements to translate a JsonElement to a Java object. There
are multiple classes to get SerializableDataTypes from(SerializableDataTypes, ApoliDataTypes, OriginsDataTypes), or you
can
create your own!

| Method Name                                                          | Description                                                                                              |
|----------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------|
| required(String key, SerializableDataBuilder<T> dataType)            | Adds a required field to the SerializableData                                                            |
| add(String key, SerializableDataBuilder<T> dataType)                 | Adds a nullable field to the SerializableData, when the field is not present, it returns null            |
| add(String key, SerializableDataBuilder<T> dataType, T defaultValue) | Adds a field to the SerializableData with a default value that is provided when the field is not present |

```java
public class ApoliExamplePower extends PowerType {
	private String welcomeMessage;

	public ApoliExamplePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority, String welcomeMessage) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(ResourceLocation.fromNamespaceAndPath("example", "apoli"))
			/* |   Field name   |   SerializableDataType instance   |   Default value   | */
			.add("welcome_message", SerializableDataTypes.STRING, "Hello World!");
	}

	public String welcomeMessage() {
		return this.welcomeMessage;
	}
}
```

By default, a PowerType is an implementation of a Listener instance, so there is no need to
add `implements org.bukkit.Listener` to your class. When extending the PowerType instance, it will
automagically register your Listener as a Listener in OriginsPaper

Now that we have created our PowerType, lets register it. Its as simple as adding calling this method:

```java
// Registers the PowerType in the PowerType "type" registry
PowerType.INSTANCE_TYPES.add(ApoliExamplePower.class); // Replace with the class of the PowerType you are registering
```

### Adding functionality to your PowerType

By default, PowerType provides Overridable methods that can be used to create your Power. The methods are as such:

| Method Name       | Description                                                                          |
|-------------------|--------------------------------------------------------------------------------------|
| tick              | Acts as the run() method in BukkitRunnables                                          |
| tick(Player)      | Ticks the PowerType on the Player that currently has the Power                       |
| tickAsync(Player) | Ticks the PowerType on the Player that currently has the Power on the AsyncScheduler |

For this example, we are going to use `tick(Player)`:

```java
public class ApoliExamplePower extends PowerType {
	private String welcomeMessage;

	public ApoliExamplePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority, String welcomeMessage) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(ResourceLocation.fromNamespaceAndPath("example", "apoli"))
			.add("welcome_message", SerializableDataTypes.STRING, "Hello World!");
	}

	public String welcomeMessage() {
		return this.welcomeMessage;
	}

	@Override
	public void tick(Player player) {
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			player.sendMessage(Component.text("You're in creative mode!"));
		}
	}
}
```

Now lets add functionality for our `welcome_message` field. If our instance was created successfully, then the
provided `welcomeMessage` value should be the one that was provided in the json, or our default value:

```java
public class ApoliExamplePower extends PowerType {
	private String welcomeMessage;

	public ApoliExamplePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority, String welcomeMessage) {
		super(key, type, name, description, hidden, condition, loadingPriority);
	}

	public static SerializableData buildFactory() {
		return PowerType.buildFactory().typedRegistry(ResourceLocation.fromNamespaceAndPath("example", "apoli"))
			.add("welcome_message", SerializableDataTypes.STRING, "Hello World!");
	}

	public String welcomeMessage() {
		return this.welcomeMessage;
	}

	@Override
	public void tick(Player player) {
		if (player.getGameMode().equals(GameMode.CREATIVE)) {
			player.sendMessage(Component.text("Your in creative mode!"));
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().sendMessage(Component.text(welcomeMessage()));
	}
}
```

And thats about it! In this section of the tutorial we have learned how to create and register a new PowerType, and add
functionality to our new power.

## Conditions

To create a new Condition, is a little bit weirder. The current Condition system wasnt really built with external
plugins in mind, but its possible to hook into the Condition system with ease.
In this tutorial, we are going to add a new entity condition that checks if the entity is currently in spectator mode.

To register a Condition, you must first go through the main Condition type class(like `EntityConditions`) to access the
conditions `register` method.
Each Condition class uses the ConditionFactory class. A ConditionFactory<T> is a builder for creating,
registering, and calling Conditions. Conditions in OriginsPaper are defined by a ResourceLocation as the associated
type,
and a Predicate to define if the condition is true or not. Each Condition type category has a different <T> value for
the ConditionFactory<T> (like entity conditions would use ConditionFactory<Entity>).

In this example, we will register a new Entity Condition.

```java
EntityConditions.register(new ConditionFactory<Entity>());
```

Now, we are going to add our `"type"` param, which defines the type associated in the json, like "origins:sprinting":

```java
register(new ConditionFactory<>(ResourceLocation.fromNamespaceAndPath("test", "example")));
```

The 2nd arg is an SerializableData, which functions exactly how the PowerType instance definer works for building! The
3rd
and final arg is a `BiPredicate<SerializableData.Instance, T>`. A SerializableData.Instance acts as an accessor for
getting values defined in the SerializableData.

```java
register(new ConditionFactory<>(
	ResourceLocation.fromNamespaceAndPath("test", "example"),
	SerializableData.serializableData(),
        (data, entity) -> {
		    return entity.isAlive();
	    }
));
```

Now we have a fully functioning condition added to the plugin! OriginsPaper should automagically setup the rest of the
registration to allow any origin to test the condition.

## Actions

Creating an Action is literally the EXACT same thing as creating a Condition, but its with an ActionFactory, which is
the same
as a ConditionFactory but with BiConsumers instead of BiPredicates.

And thats it! Almost the exact same thing as Condition registration, and the same process in the end aswell.

## PowerHolderComponent

The PowerHolderComponent class is the main utility class for accessing information about Powers. It holds and manages
the switching of origins, adding and removal of powers, and a few misc things related to the PersistentDataContainer.
Realistically, you only need to know a few things, how to get the powers of an entity, how to apply and remove powers,
and setting the origin of the entity.

### Retrieving the Powers of the Entity

Lets start with getting any and all Powers of the entity. The method PowerHolderComponent.getPowers(Entity) returns all
the Powers on all layers of the Entity, however if it is not a Player instance, it will return an empty list due to
Entities not being able to hold Powers(yet):

```java
Entity entity = /*your entity instance*/;
List<PowerType> powers = PowerHolderComponent.getPowers(entity);
```

Now lets get all the Powers of a specific type. There are 2 ways of retrieving this information.
1, is by providing the String of the type key, like "apoli:multiple" or "apoli:phasing":

```java
Entity entity = /*your entity instance*/;
List<PowerType> phasingPowers = PowerHolderComponent.getPowers(entity, "apoli:phasing"); // Returns all the powers of the phasing powertype
```

2, is by providing the PowerType *class* inside the source. This way allows OriginsPaper to return that PowerType
instance
instead of the raw PowerType aswell, and is a lot faster than the 1st way. Lets do the same example as before, but with
the other method.

```java
Entity entity = /*your entity instance*/;
List<Phasing> phasingPowers = PowerHolderComponent.getPowers(entity, Phasing.class);
```

Now we are going to get a *specific* Power from its *tag* instance now. A PowerType tag is the NamespacedKey assigned to
the Power upon registration, and works the same way as the normal Minecraft registration.

```java
Entity entity = /*your entity instance*/;
PowerType phasingPower = PowerHolderComponent.getPower(entity, "origins:phasing");
```

### Applying and Removing Powers

Now lets apply a Power. To simplify the process, there is a different class that does this for you called
the `PowerUtils` class. This class contains 2 methods, `grantPower` and `removePower`. Each method has the same args:

```
(Nullable) CommandSender - allows for sending the return message from the `/power` command. If null, it wont send.
PowerType - the Power to grant or remove
Player - the Player to act upon
boolean - suppresses the return message from the CommandSender and the debug statement printed in console if true.
```

### Checking if a Player has a Power

Checking if an Entity has a Power is similar to retrieving them. We have 2 methods for this, using the PowerType class,
and the String tag.
Lets do it with a provided PowerType arg:

```java
Entity entity = /*your entity instance*/;
    if (PowerHolderComponent.hasPowerType(entity, Phasing.class)) {
	    entity.sendMessage(Component.text("You have a phasing power!"));
	}
```

When using the one with a provided String, it takes a Power *tag*, not a Power *type*:

```java
Entity entity = /*your entity instance*/;
    if (PowerHolderComponent.hasPower(entity, "origins:phasing")){
        entity.sendMessage(Component.text("You have the phantom phasing power!"));
    }
```

### Setting and Getting the Origin of a Player

To set the Origin of a Player, you need to get the Origin object, and Layer object. There are a few ways of getting a
Layer or Origin, the easiest being in the `CraftApoli` class, which serves as a core utility class for the CraftApoli
system. In this example, we will use the methods `getOriginFromTag` - returns the origin matching the tag, null if not
found and `getLayersFromRegistry` - returns all the layers in the registrar.

Setting an origin is quite simple, and requires an Origin and Layer to set it:

```java
Player player = /*your player instance*/;
/**
 * Sets the origin on the default layer provided by Origins/OriginsPaper
 */
PowerHolderComponent.setOrigin(player, CraftApoli.getOriginFromTag("origins:elytrian"), CraftApoli.getLayerFromTag("origins:origin"));

/**
 * Sets the origin on ALL layers in the OriginsPaper registrar
 */
	for (OriginLayer layer : CraftApoli.getLayersFromRegistry()){
	    PowerHolderComponent.setOrigin(player, CraftApoli.getOriginFromTag("origins:elytrian"), layer);
	}
```

To get the Origin on a Player, there are a few ways. In OriginsPaper, each Layer for the player has an assigned Origin
to
it in a Map inside the PowerHolderComponent. To get a specific layer, you can provide a Layer argument to
the `getOrigin` method and return that specific origin. If the Layer isnt specified, it will return the full Layer ->
Origin Map.

```java
Player player = /*your player instance*/;
/**
 * Returns the Layer -> Origin Map. NotNull
 */
Map<OriginLayer, Origin> layerToOriginMap = PowerHolderComponent.getOrigin(player);

/**
 * Returns the Origin found on the origins:origin Layer. Null if not found.
 */
Origin originsOrigin = PowerHolderComponent.getOrigin(player, CraftApoli.getLayerFromTag("origins:origin"));
```

And thats it! That should cover everything important that you need to know for the OriginsPaper CraftApoli system. Ping
@dueris in the discord server if you have any questions regarding the API or specific functionalities of the plugin.
