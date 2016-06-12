

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by casde & Joshua on 9-6-2016.
 */
public class Card implements Comparable<Card>
{
    private Rectangle2D rectangle;
    private int cardImage;
    private boolean flipped;
    private Color color;

    public Card(int x, int y, int width, int height, int cardImage)
    {
        rectangle = new Rectangle2D.Double(x,y,width,height);
        switch(cardImage)
        {
            case 1:
                color = Color.RED;
                break;

            case 2:
                color = Color.ORANGE;
                break;

            case 3:
                color = Color.BLUE;
                break;

            case 4:
                color = Color.GREEN;
                break;

            case 5:
                color = Color.GRAY;
                break;

            case 6:
                color = Color.YELLOW;
                break;

            case 7:
                color = Color.MAGENTA;
                break;

            case 8:
                color = Color.CYAN;
                break;
        }
    }

    public Rectangle2D getRect()
    {
        return rectangle;
    }

    public int getX()
    {
    double x = rectangle.getX()/rectangle.getWidth();
    if(x > 1 && x < 2)
    {
        return (int) (x -= 0.1);
    }

    if(x > 2 && x < 3)
    {
        return (int) (x -= 0.2);
    }

    if(x > 3 && x < 4)
    {
        return (int) (x -= 0.3);
    }

    return 0;
    }

    public int getY()
    {
        double y = rectangle.getY()/rectangle.getHeight();
        if(y > 1 && y < 2)
        {
            return (int) (y -= 0.1);
        }

        if(y > 2 && y < 3)
        {
            return (int) (y -= 0.2);
        }

        if(y > 3 && y < 4)
        {
            return (int) (y -= 0.3);
        }

        return 0;
    }

    public int getCardImage() {
        return cardImage;
    }

    public void setCardImage(int cardImage) {
        this.cardImage = cardImage;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }


    public void draw(Graphics2D g2)
    {
        if(!flipped)
        {
            g2.fill(rectangle);
        }
        else
        {
            Color oldColor = g2.getColor();
            g2.setColor(color);
            g2.fill(rectangle);
            g2.setColor(oldColor);
        }

    }

    public void flip()
    {
        flipped =! flipped;
    }

    @Override
    public int compareTo(Card o) {
        if(this.color == o.getColor())
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }
}
