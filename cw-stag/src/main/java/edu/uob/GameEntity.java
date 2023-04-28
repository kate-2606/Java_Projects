package edu.uob;

public abstract class GameEntity
{
    //map is game entity?
    private String name;
    private String description;

    GameLocation location;

    public GameEntity(String name, String description, GameLocation location)
    {
        this.name = name;
        this.description = description;
        this.location = location;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public void setLocation(GameLocation location) { this.location = location; }

    public GameLocation getLocation() { return this.location; }
}
