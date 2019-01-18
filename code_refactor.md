# Code refactoring case study for mutation observer
This file specify all the details of code refactoring case study

# Rule 1:
test_distance > 5 && (loop(loop)) <= 0 && is_nested = 0 && is_public = 0 && XMET > 4 && (loop) <= 0 && NOCL <= 9 && non-void_percent <= 0.42
## Case 1.1: org.jfree.chart.StandardChartTheme:applyToCategoryAxis
```java
1455	 	    /**
1456	 	     * Applies the attributes for this theme to a {@link CategoryAxis}.
1457	 	     *
1458	 	     * @param axis  the axis ({@code null} not permitted).
1459	 	     */
1460	 	    protected void applyToCategoryAxis(CategoryAxis axis) {
1461	1	        axis.setLabelFont(this.largeFont);
1462	1	        axis.setLabelPaint(this.axisLabelPaint);
1463	1	        axis.setTickLabelFont(this.regularFont);
1464	1	        axis.setTickLabelPaint(this.tickLabelPaint);
1465	1	        if (axis instanceof SubCategoryAxis) {
1466	 	            SubCategoryAxis sca = (SubCategoryAxis) axis;
1467	1	            sca.setSubLabelFont(this.regularFont);
1468	1	            sca.setSubLabelPaint(this.tickLabelPaint);
1469	 	        }
1470	 	    }

1462		1. removed call to org/jfree/chart/axis/CategoryAxis::setLabelFont → SURVIVED
1463		1. removed call to org/jfree/chart/axis/CategoryAxis::setLabelPaint → SURVIVED
1464		1. removed call to org/jfree/chart/axis/CategoryAxis::setTickLabelFont → SURVIVED
1465		1. removed call to org/jfree/chart/axis/CategoryAxis::setTickLabelPaint → SURVIVED
1466		1. negated conditional → KILLED
1468		1. removed call to org/jfree/chart/axis/SubCategoryAxis::setSubLabelFont → NO_COVERAGE
1469		1. removed call to org/jfree/chart/axis/SubCategoryAxis::setSubLabelPaint → NO_COVERAGE
```

### Code refactoring:
#### Attempt 1:
test_distance > 5 → add a direct test → work!!!
```java
@Test
public void testApplyToCategoryAxis(){
    StandardChartTheme t1 = new StandardChartTheme("Name");
    CategoryAxis categoryAxis = new CategoryAxis();
    t1.applyToCategoryAxis(categoryAxis);
    assertTrue(t1.getLargeFont() == categoryAxis.getLabelFont());
    assertTrue(t1.getAxisLabelPaint() == categoryAxis.getLabelPaint());
    assertTrue(t1.getRegularFont() == categoryAxis.getTickLabelFont());
    assertTrue(t1.getTickLabelPaint() == categoryAxis.getTickLabelPaint());
}
```
## Case 1.2: org.jfree.chart.StandardChartTheme:applyToXYItemRenderer
```java
1572	 	    /**
1573	 	     * Applies the settings of this theme to the specified renderer.
1574	 	     *
1575	 	     * @param renderer  the renderer ({@code null} not permitted).
1576	 	     */
1577	 	    protected void applyToXYItemRenderer(XYItemRenderer renderer) {
1578	1	        Args.nullNotPermitted(renderer, "renderer");
1579	1	        if (renderer instanceof AbstractRenderer) {
1580	1	            applyToAbstractRenderer((AbstractRenderer) renderer);
1581	 	        }
1582	1	        renderer.setDefaultItemLabelFont(this.regularFont);
1583	1	        renderer.setDefaultItemLabelPaint(this.itemLabelPaint);
1584	1	        if (renderer instanceof XYBarRenderer) {
1585	 	            XYBarRenderer br = (XYBarRenderer) renderer;
1586	1	            br.setBarPainter(this.xyBarPainter);
1587	1	            br.setShadowVisible(this.shadowVisible);
1588	 	        }
1589	 	    }

1578		1. removed call to org/jfree/chart/util/Args::nullNotPermitted → SURVIVED
1579		1. negated conditional → SURVIVED
1580		1. removed call to org/jfree/chart/StandardChartTheme::applyToAbstractRenderer → SURVIVED
1582		1. removed call to org/jfree/chart/renderer/xy/XYItemRenderer::setDefaultItemLabelFont → SURVIVED
1583		1. removed call to org/jfree/chart/renderer/xy/XYItemRenderer::setDefaultItemLabelPaint → SURVIVED
1584		1. negated conditional → KILLED
1586		1. removed call to org/jfree/chart/renderer/xy/XYBarRenderer::setBarPainter → SURVIVED
1587		1. removed call to org/jfree/chart/renderer/xy/XYBarRenderer::setShadowVisible → SURVIVED
```

### Code refactoring:
#### Attempt 1:
test_distance > 5 → add 2 direct test, one for null case, the other for not null case → 1578, 1582, 1584, 1586, 1587 are killed → 1579,1580 cannot be killed because it is impossible to create a XYItemRenderer (interface) or AbstractRenderer (abstract class) instance for testing purpose. 1583 cannot be killed is because the default value of ItemLabelPaint in StandardChartTheme is Color.black, while in XYBarRenderer, the default value of that is the same. The removal of setDefaultItemLabelPaint method call does not change the concequence of ItemLabelPaint value.
```java
@Test(expected = IllegalArgumentException.class)
public void testApplyToXYItemRendererNull(){
    StandardChartTheme t1 = new StandardChartTheme("Name");
    t1.applyToXYItemRenderer(null);
}

@Test
public void testApplyToXYItemRendererNotNull(){
  StandardChartTheme t1 = new StandardChartTheme("Name");
  XYBarRenderer renderer = new XYBarRenderer(2.1);
  t1.applyToXYItemRenderer(renderer);
  assertTrue(t1.getRegularFont() == renderer.getDefaultItemLabelFont());
  assertTrue(t1.getItemLabelPaint() == renderer.getDefaultItemLabelPaint());
  assertTrue(t1.getXYBarPainter() == renderer.getBarPainter());
  assertTrue(t1.isShadowVisible() == renderer.getShadowsVisible());
}
```
#### Attempt 2:
test_distance > 5 → improve the second direct test → work!! Except 1579,1580 (Line 1579,1580 considered the dead code in this case)
```java
@Test
public void testApplyToXYItemRenderer(){
    StandardChartTheme t1 = (StandardChartTheme) createDarknessTheme();
    XYBarRenderer renderer = new XYBarRenderer(2.1);
    t1.applyToXYItemRenderer(renderer);
    assertTrue(t1.getRegularFont() == renderer.getDefaultItemLabelFont());
    assertTrue(t1.getItemLabelPaint() == renderer.getDefaultItemLabelPaint());
    assertTrue(t1.getXYBarPainter() == renderer.getBarPainter());
    assertTrue(t1.isShadowVisible() == renderer.getShadowsVisible());
}
```
## Case 1.3: org.jfree.chart.plot.MeterPlot:drawValueLabel
```java
1139	 	    /**
1140	 	     * Draws the value label just below the center of the dial.
1141	 	     *
1142	 	     * @param g2  the graphics device.
1143	 	     * @param area  the plot area.
1144	 	     */
1145	 	    protected void drawValueLabel(Graphics2D g2, Rectangle2D area) {
1146	1	        g2.setFont(this.valueFont);
1147	1	        g2.setPaint(this.valuePaint);
1148	 	        String valueStr = "No value";
1149	1	        if (this.dataset != null) {
1150	 	            Number n = this.dataset.getValue();
1151	1	            if (n != null) {
1152	 	                valueStr = this.tickLabelFormat.format(n.doubleValue()) + " "
1153	 	                         + this.units;
1154	 	            }
1155	 	        }
1156	 	        float x = (float) area.getCenterX();
1157	1	        float y = (float) area.getCenterY() + DEFAULT_CIRCLE_SIZE;
1158	 	        TextUtils.drawAlignedString(valueStr, g2, x, y,
1159	 	                TextAnchor.TOP_CENTER);
1160	 	    }


1146	1	removed call to java/awt/Graphics2D::setFont → SURVIVED
1147	1	removed call to java/awt/Graphics2D::setPaint → SURVIVED
1149	1	negated conditional → SURVIVED
1151	1	negated conditional → SURVIVED
1157	1	Replaced float addition with subtraction → SURVIVED
```
### Code refactoring:
#### Attempt 1:
test_distance > 5 → add one direct test → 1146, 1147, 1149 are killed 1151 → and 1157 are not killed because this method is void. The changing state of TextUtils.drawAlignedString() function cannot be assessed.
```java
@Test
public void testDrawValueLabel(){
    MeterPlot p1 = new MeterPlot();
    BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    Rectangle2D area = new Rectangle(0, 0, 1, 1);
    p1.drawValueLabel(g2,area);
    assertTrue(g2.getFont() == p1.getValueFont());
    assertTrue(g2.getPaint() == p1.getValuePaint());
}
```
#### Attempt 2:
non-void_percent <= 0.42 → change void to return Rectangle2D → improve direct test → all killed!!
```java
protected Rectangle2D drawValueLabel(Graphics2D g2, Rectangle2D area) {
    g2.setFont(this.valueFont);
    g2.setPaint(this.valuePaint);
    String valueStr = "No value";
    if (this.dataset != null) {
        Number n = this.dataset.getValue();
        if (n != null) {
            valueStr = this.tickLabelFormat.format(n.doubleValue()) + " "
                     + this.units;
        }
    }
    float x = (float) area.getCenterX();
    float y = (float) area.getCenterY() + DEFAULT_CIRCLE_SIZE;
    return TextUtils.drawAlignedString(valueStr, g2, x, y,
            TextAnchor.TOP_CENTER);
}

@Test
public void testDrawValueLabel(){
    MeterPlot p1 = new MeterPlot(new DefaultValueDataset(1.23));
    BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    Rectangle2D area = new Rectangle(0, 0, 1, 1);
    Rectangle2D drawArea = p1.drawValueLabel(g2,area);
    assertTrue(g2.getFont() == p1.getValueFont());
    assertTrue(g2.getPaint() == p1.getValuePaint());
    assertEquals(0.5,drawArea.getCenterX(),0.01);
    assertEquals(18.8671875,drawArea.getCenterY(),0.01);
    assertEquals(15.0,drawArea.getHeight(),0.01);
    assertEquals(64.0,drawArea.getWidth(),0.01);
}
```
## Case 1.4: org.jfree.chart.plot.MeterPlot:drawTick
```java
1071	 	    /**
1072	 	     * Draws a tick on the dial.
1073	 	     *
1074	 	     * @param g2  the graphics device.
1075	 	     * @param meterArea  the meter area.
1076	 	     * @param value  the tick value.
1077	 	     * @param label  a flag that controls whether or not a value label is drawn.
1078	 	     */
1079	 	    protected void drawTick(Graphics2D g2, Rectangle2D meterArea,
1080	 	                            double value, boolean label) {
1081	 	
1082	 	        double valueAngle = valueToAngle(value);
1083	 	
1084	 	        double meterMiddleX = meterArea.getCenterX();
1085	 	        double meterMiddleY = meterArea.getCenterY();
1086	 	
1087	1	        g2.setPaint(this.tickPaint);
1088	1	        g2.setStroke(new BasicStroke(2.0f));
1089	 	
1090	 	        double valueP2X;
1091	 	        double valueP2Y;
1092	 	
1093	2	        double radius = (meterArea.getWidth() / 2) + DEFAULT_BORDER_SIZE;
1094	1	        double radius1 = radius - 15;
1095	 	
1096	2	        double valueP1X = meterMiddleX
1097	2	                + (radius * Math.cos(Math.PI * (valueAngle / 180)));
1098	2	        double valueP1Y = meterMiddleY
1099	2	                - (radius * Math.sin(Math.PI * (valueAngle / 180)));
1100	 	
1101	2	        valueP2X = meterMiddleX
1102	2	                + (radius1 * Math.cos(Math.PI * (valueAngle / 180)));
1103	2	        valueP2Y = meterMiddleY
1104	2	                - (radius1 * Math.sin(Math.PI * (valueAngle / 180)));
1105	 	
1106	 	        Line2D.Double line = new Line2D.Double(valueP1X, valueP1Y, valueP2X,
1107	 	                valueP2Y);
1108	1	        g2.draw(line);
1109	 	
1110	2	        if (this.tickLabelsVisible && label) {
1111	 	
1112	 	            String tickLabel =  this.tickLabelFormat.format(value);
1113	1	            g2.setFont(this.tickLabelFont);
1114	1	            g2.setPaint(this.tickLabelPaint);
1115	 	
1116	 	            FontMetrics fm = g2.getFontMetrics();
1117	 	            Rectangle2D tickLabelBounds
1118	 	                = TextUtils.getTextBounds(tickLabel, g2, fm);
1119	 	
1120	 	            double x = valueP2X;
1121	 	            double y = valueP2Y;
1122	2	            if (valueAngle == 90 || valueAngle == 270) {
1123	2	                x = x - tickLabelBounds.getWidth() / 2;
1124	 	            }
1125	4	            else if (valueAngle < 90 || valueAngle > 270) {
1126	1	                x = x - tickLabelBounds.getWidth();
1127	 	            }
1128	8	            if ((valueAngle > 135 && valueAngle < 225)
1129	 	                    || valueAngle > 315 || valueAngle < 45) {
1130	2	                y = y - tickLabelBounds.getHeight() / 2;
1131	 	            }
1132	 	            else {
1133	2	                y = y + tickLabelBounds.getHeight() / 2;
1134	 	            }
1135	1	            g2.drawString(tickLabel, (float) x, (float) y);
1136	 	        }
1137	 	    }

1087		1. removed call to java/awt/Graphics2D::setPaint → SURVIVED
1088		1. removed call to java/awt/Graphics2D::setStroke → SURVIVED
1093		1. Replaced double division with multiplication → SURVIVED
                        2. Replaced double addition with subtraction → SURVIVED
1094		1. Replaced double subtraction with addition → SURVIVED
1096		1. Replaced double division with multiplication → SURVIVED
                        2. Replaced double multiplication with division → SURVIVED
1097		1. Replaced double multiplication with division → SURVIVED
                        2. Replaced double addition with subtraction → SURVIVED
1098		1. Replaced double division with multiplication → SURVIVED
                        2. Replaced double multiplication with division → SURVIVED
1099		1. Replaced double multiplication with division → SURVIVED
                        2. Replaced double subtraction with addition → SURVIVED
1101		1. Replaced double division with multiplication → SURVIVED
                        2. Replaced double multiplication with division → SURVIVED
1102		1. Replaced double multiplication with division → SURVIVED
                        2. Replaced double addition with subtraction → SURVIVED
1103		1. Replaced double division with multiplication → SURVIVED
                        2. Replaced double multiplication with division → SURVIVED
1104		1. Replaced double multiplication with division → SURVIVED
                        2. Replaced double subtraction with addition → SURVIVED
1108		1. removed call to java/awt/Graphics2D::draw → SURVIVED
1110		1. negated conditional → SURVIVED
                        2. negated conditional → SURVIVED
1113		1. removed call to java/awt/Graphics2D::setFont → SURVIVED
1114		1. removed call to java/awt/Graphics2D::setPaint → SURVIVED
1122		1. negated conditional → SURVIVED 2. negated conditional → SURVIVED
1123		1. Replaced double division with multiplication → NO_COVERAGE
                        2. Replaced double subtraction with addition → NO_COVERAGE
1125		1. changed conditional boundary → SURVIVED
                        2. changed conditional boundary → SURVIVED
                        3. negated conditional → SURVIVED 4. negated conditional → SURVIVED
1126		1. Replaced double subtraction with addition → SURVIVED
1128		1. changed conditional boundary → SURVIVED
                        2. changed conditional boundary → SURVIVED
                        3. changed conditional boundary → SURVIVED
                        4. changed conditional boundary → SURVIVED
                        5. negated conditional → SURVIVED
                        6. negated conditional → SURVIVED
                        7. negated conditional → SURVIVED
                        8. negated conditional → SURVIVED
1130		1. Replaced double division with multiplication → SURVIVED
                        2. Replaced double subtraction with addition → SURVIVED
1133		1. Replaced double division with multiplication → SURVIVED
                        2. Replaced double addition with subtraction → SURVIVED
1135		1. removed call to java/awt/Graphics2D::drawString → SURVIVED
```

### Code refactoring:
#### Attempt 1:
test_distance > 5 → add two direct tests (two conditions) → 1087, 1088, 1110, 1113, 1114 are killed → The rest are not killed because this method is void. The changing states of draw() and drawString() functions cannot be assessed.
```java
@Test
public void testDrawTickLableFalse(){
    MeterPlot p1 = new MeterPlot(new DefaultValueDataset(1.23));
    BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    g2.setPaint(Color.BLACK);  // to differ default value of tickPaint
    Rectangle2D area = new Rectangle(0, 0, 1, 1);
    p1.drawTick(g2,area,1.0,false);
    assertTrue(g2.getPaint()==p1.getTickPaint());
    assertEquals(2.0,((BasicStroke)g2.getStroke()).getLineWidth(),0.01);
}

@Test
public void testDrawTickLableTrue(){
    MeterPlot p1 = new MeterPlot(new DefaultValueDataset(1.23));
    BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    Rectangle2D area = new Rectangle(0, 0, 1, 1);
    p1.drawTick(g2,area,1.0,true);
    assertTrue(g2.getFont() == p1.getTickLabelFont());
    assertTrue(g2.getPaint()==p1.getTickLabelPaint());
}
```
#### Attempt 2:
non-void_percent <= 0.42 → change void to return DrawTickResults (new class) → improve direct test → 1113, 1134.1, 1134.2, 1137.4 and 1144 are not possible to be killed. 1113 and 1144 are method calls to third-party library, and these methods are void, cannot do code refactoring.  1134.1, 1134.2 and 1137.4 are dead code. The conditions boundaries replaces cannot be reached.
```java
package org.jfree.chart.plot;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class DrawTickResults {
    public Line2D.Double line;
    public float stringX;
    public float stringY;
    public Rectangle2D tickLabelBounds;
    public double valueToAngle;

    public DrawTickResults (double valueToAngle,Line2D.Double line,
                            Rectangle2D tickLabelBounds,
                            float stringX, float stringY){
        this.valueToAngle = valueToAngle;
        this.line = line;
        this.tickLabelBounds = tickLabelBounds;
        this.stringX = stringX;
        this.stringY = stringY;
    }
}

    protected DrawTickResults drawTick(Graphics2D g2, Rectangle2D meterArea,
                            double value, boolean label) {

        double valueAngle = valueToAngle(value);

        double meterMiddleX = meterArea.getCenterX();
        double meterMiddleY = meterArea.getCenterY();

        g2.setPaint(this.tickPaint);
        g2.setStroke(new BasicStroke(2.0f));

        double valueP2X;
        double valueP2Y;

        double radius = (meterArea.getWidth() / 2) + DEFAULT_BORDER_SIZE;
        double radius1 = radius - 15;

        double valueP1X = meterMiddleX
                + (radius * Math.cos(Math.PI * (valueAngle / 180)));
        double valueP1Y = meterMiddleY
                - (radius * Math.sin(Math.PI * (valueAngle / 180)));

        valueP2X = meterMiddleX
                + (radius1 * Math.cos(Math.PI * (valueAngle / 180)));
        valueP2Y = meterMiddleY
                - (radius1 * Math.sin(Math.PI * (valueAngle / 180)));

        Line2D.Double line = new Line2D.Double(valueP1X, valueP1Y, valueP2X,
                valueP2Y);
        g2.draw(line);
        // code refactoring
        DrawTickResults drawTickResults = new DrawTickResults(valueAngle,line,null,-1,-1);

        if (this.tickLabelsVisible && label) {

            String tickLabel =  this.tickLabelFormat.format(value);
            g2.setFont(this.tickLabelFont);
            g2.setPaint(this.tickLabelPaint);

            FontMetrics fm = g2.getFontMetrics();
            Rectangle2D tickLabelBounds
                = TextUtils.getTextBounds(tickLabel, g2, fm);
            // code refactoring
            drawTickResults.tickLabelBounds = tickLabelBounds;

            double x = valueP2X;
            double y = valueP2Y;
            if (valueAngle == 90 || valueAngle == 270) {
                x = x - tickLabelBounds.getWidth() / 2;
            }
            else if (valueAngle < 90 || valueAngle > 270) {
                x = x - tickLabelBounds.getWidth();
            }
            if ((valueAngle > 135 && valueAngle < 225)
                    || valueAngle > 315 || valueAngle < 45) {
                y = y - tickLabelBounds.getHeight() / 2;
            }
            else {
                y = y + tickLabelBounds.getHeight() / 2;
            }
            g2.drawString(tickLabel, (float) x, (float) y);
            // code refactoring
            drawTickResults.stringX = (float) x;
            drawTickResults.stringY = (float) y;
        }
        return drawTickResults;
    }

    @Test
    public void testDrawTickLableFalse(){
        MeterPlot p1 = new MeterPlot();
        BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setPaint(Color.BLACK);  // to differ default value of tickPaint
        Rectangle2D area = new Rectangle(0, 0, 1, 1);
        DrawTickResults drawTickResults = p1.drawTick(g2,area,1.0,false);
        assertTrue(g2.getPaint()==p1.getTickPaint());
        assertEquals(2.0,((BasicStroke)g2.getStroke()).getLineWidth(),0.01);
        assertEquals(-2.0887088324251337, drawTickResults.line.getX1(), 1e-15);
        assertEquals(9.00575759225401, drawTickResults.line.getX2(),1e-15);
        assertEquals(2.855543797284207,drawTickResults.line.getY1(),1e-15);
        assertEquals(-7.239643905362394, drawTickResults.line.getY2(),1e-15);
        assertEquals(-1, drawTickResults.stringX,0.01);
        assertEquals(-1, drawTickResults.stringY,0.01);
    }

    @Test
    public void testDrawTickLableTrue90(){
        MeterPlot p1 = new MeterPlot();
        BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        Rectangle2D area = new Rectangle(-1, -2, 1, 100);
        DrawTickResults drawTickResults = p1.drawTick(g2,area,50.0,true);
        assertTrue(g2.getFont() == p1.getTickLabelFont());
        assertTrue(g2.getPaint()==p1.getTickLabelPaint());
        assertEquals(-7.5,drawTickResults.stringX,1e-6);
        assertEquals(66.0,drawTickResults.stringY,1e-6);
        assertEquals(14.0,drawTickResults.tickLabelBounds.getWidth(),0.01);
        assertEquals(13.0,drawTickResults.tickLabelBounds.getHeight(),0.01);
        assertEquals(7.0,drawTickResults.tickLabelBounds.getCenterX(),0.01);
        assertEquals(-3.5,drawTickResults.tickLabelBounds.getCenterY(),0.01);
        assertEquals(90.0,drawTickResults.valueToAngle,0.01);
    }

    @Test
    public void testDrawTickLableTrue135(){
        MeterPlot p1 = new MeterPlot();
        BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        Rectangle2D area = new Rectangle(-1, -2, 1, 100);
        DrawTickResults drawTickResults = p1.drawTick(g2,area,33.33333333333333,true);
        assertTrue(g2.getFont() == p1.getTickLabelFont());
        assertTrue(g2.getPaint()==p1.getTickLabelPaint());
        assertEquals(7.631728,drawTickResults.stringX,1e-6);
        assertEquals(62.63173,drawTickResults.stringY,1e-6);
        assertEquals(37.0,drawTickResults.tickLabelBounds.getWidth(),0.01);
        assertEquals(13.0,drawTickResults.tickLabelBounds.getHeight(),0.01);
        assertEquals(18.5,drawTickResults.tickLabelBounds.getCenterX(),0.01);
        assertEquals(-3.5,drawTickResults.tickLabelBounds.getCenterY(),0.01);
        assertEquals(135.0,drawTickResults.valueToAngle,0.01);
    }

    @Test
    public void testDrawTickLableTrue225(){
        MeterPlot p1 = new MeterPlot();
        BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        Rectangle2D area = new Rectangle(-1, -2, 1, 100);
        DrawTickResults drawTickResults = p1.drawTick(g2,area,0.0,true);
        assertTrue(g2.getFont() == p1.getTickLabelFont());
        assertTrue(g2.getPaint()==p1.getTickLabelPaint());
        assertEquals(7.631728,drawTickResults.stringX,1e-6);
        assertEquals(46.36827,drawTickResults.stringY,1e-6);
        assertEquals(7.0,drawTickResults.tickLabelBounds.getWidth(),0.01);
        assertEquals(13.0,drawTickResults.tickLabelBounds.getHeight(),0.01);
        assertEquals(3.5,drawTickResults.tickLabelBounds.getCenterX(),0.01);
        assertEquals(-3.5,drawTickResults.tickLabelBounds.getCenterY(),0.01);
        assertEquals(225.0,drawTickResults.valueToAngle,0.01);
    }

    @Test
    public void testDrawTickLableTrue315(){
        MeterPlot p1 = new MeterPlot();
        BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        Rectangle2D area = new Rectangle(-1, -2, 1, 100);
        DrawTickResults drawTickResults = p1.drawTick(g2,area,-33.33333333333333,true);
        assertTrue(g2.getFont() == p1.getTickLabelFont());
        assertTrue(g2.getPaint()==p1.getTickLabelPaint());
        assertEquals(-51.63173,drawTickResults.stringX,1e-6);
        assertEquals(46.36827,drawTickResults.stringY,1e-6);
        assertEquals(43.0,drawTickResults.tickLabelBounds.getWidth(),0.01);
        assertEquals(13.0,drawTickResults.tickLabelBounds.getHeight(),0.01);
        assertEquals(21.5,drawTickResults.tickLabelBounds.getCenterX(),0.01);
        assertEquals(-3.5,drawTickResults.tickLabelBounds.getCenterY(),0.01);
        assertEquals(315.0,drawTickResults.valueToAngle,0.01);
    }

    @Test
    public void testDrawTickLableTrue198(){
        MeterPlot p1 = new MeterPlot();
        BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        Rectangle2D area = new Rectangle(-1, -2, 1, 100);
        DrawTickResults drawTickResults = p1.drawTick(g2,area,10.0,true);
        assertTrue(g2.getFont() == p1.getTickLabelFont());
        assertTrue(g2.getPaint()==p1.getTickLabelPaint());
        assertEquals(10.43715,drawTickResults.stringX,1e-6);
        assertEquals(37.946304,drawTickResults.stringY,1e-6);
        assertEquals(14.0,drawTickResults.tickLabelBounds.getWidth(),0.01);
        assertEquals(13.0,drawTickResults.tickLabelBounds.getHeight(),0.01);
        assertEquals(7.0,drawTickResults.tickLabelBounds.getCenterX(),0.01);
        assertEquals(-3.5,drawTickResults.tickLabelBounds.getCenterY(),0.01);
        assertEquals(198.0,drawTickResults.valueToAngle,0.01);
    }

    @Test
    public void testDrawTickLableTrueMinus100(){
        MeterPlot p1 = new MeterPlot();
        BufferedImage image = new BufferedImage(3, 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        Rectangle2D area = new Rectangle(-1, -2, 1, 100);
        DrawTickResults drawTickResults = p1.drawTick(g2,area,120.37037037037037,true);
        assertTrue(g2.getFont() == p1.getTickLabelFont());
        assertTrue(g2.getPaint()==p1.getTickLabelPaint());

        assertEquals(-35.503044,drawTickResults.stringX,1e-6);
        assertEquals(30.174711,drawTickResults.stringY,1e-6);
        assertEquals(37.0,drawTickResults.tickLabelBounds.getWidth(),0.01);
        assertEquals(13.0,drawTickResults.tickLabelBounds.getHeight(),0.01);
        assertEquals(18.5,drawTickResults.tickLabelBounds.getCenterX(),0.01);
        assertEquals(-3.5,drawTickResults.tickLabelBounds.getCenterY(),0.01);
        assertEquals(-100.0,drawTickResults.valueToAngle,0.01);
    }
```
# Rule 2:
test_distance > 5 && (loop(loop)) <= 0 && is_nested = 0 && is_public = 0 && XMET > 4 && (loop) <= 0 && NOCL > 9
## Case 2.1: org.jfree.chart.axis.SymbolAxis:drawGridBands
```java
154	 	    /**
155	 	     * Similar to {@link Color#darker()}.
156	 	     * <p>
157	 	     * The essential difference is that this method
158	 	     * maintains the alpha-channel unchanged<br>
159	 	     *
160	 	     * @param paint a {@code Color}
161	 	     *
162	 	     * @return a darker version of the {@code Color}
163	 	     */
164	 	    private static Color darker(Color paint) {
165	1	        return new Color(
166	1	                (int)(paint.getRed  () * FACTOR),
167	1	                (int)(paint.getGreen() * FACTOR),
168	1	                (int)(paint.getBlue () * FACTOR), paint.getAlpha());
169	 	    }


165		1. mutated return of Object value for org/jfree/chart/util/PaintAlpha::darker to ( if (x != null) null else throw new RuntimeException ) → NO_COVERAGE
166		1. Replaced double multiplication with division → NO_COVERAGE
167		1. Replaced double multiplication with division → NO_COVERAGE
168		1. Replaced double multiplication with division → NO_COVERAGE
```
### Code refactoring:
#### Attempt 1:
is_public = 0 → code refactoring: private to public/protected → add direct test → all killed!
```java
protected static Color darker(Color paint) {
    return new Color(
            (int)(paint.getRed  () * FACTOR),
            (int)(paint.getGreen() * FACTOR),
            (int)(paint.getBlue () * FACTOR), paint.getAlpha());
}
@Test
public void testDarker(){
    Color paint = new Color(10,20,30);
    Color darker = PaintAlpha.darker(paint);
    assertEquals(7,darker.getRed());
    assertEquals(14,darker.getGreen());
    assertEquals(21,darker.getBlue());

}
```
# Rule 3:
test_distance > 5 && (loop(loop)) <= 0 && is_nested = 0 && is_public = 1 && NOCL <= 4 && NOCL > 0 && is_static = 0 && getter_percent <= 0.01 && HBUG <= 0.02 && method_length > 3

## Case 3.1: org.apache.commons.lang3.builder.IDKey:hashCode		
```java
46	 	        /**
47	 	         * returns hash code - i.e. the system identity hashcode.
48	 	         * @return the hashcode
49	 	         */
50	 	        @Override
51	 	        public int hashCode() {
52	1	           return id;
53	 	        }

52		1. replaced return of integer sized value with (x == 0 ? 1 : 0) → SURVIVED
```
### Code refactoring:
#### Attempt 1:  
test_distance > 5 → add direct test → work!!!
```java
@Test
public void testHashCode(){
    IDKey idKey = new IDKey(new Integer(123));
    assertEquals(989794870,idKey.hashCode());
}
```
## Case 3.2: org.apache.commons.math3.random.ISAACRandom:setSeed
```java
113	 	    /** {@inheritDoc} */
114	 	    @Override
115	 	    public void setSeed(long seed) {
116	3	        setSeed(new int[]{(int) (seed >>> 32), (int) (seed & 0xffffffffL)});
117	 	    }

116	 1. Replaced Unsigned Shift Right with Shift Left → SURVIVED
        2. Replaced bitwise AND with OR → SURVIVED
        3. removed call to org/apache/commons/math3/random/ISAACRandom::setSeed → KILLED
```
### Code refactoring:
#### Attempt 1:  
test_distance > 5 → add direct test → work!!!
```java
@Test
public void testSetSeed(){
    ISAACRandom isaacRandom = new ISAACRandom();
    isaacRandom.setSeed(123456789012345678L);
    assertEquals(-1876928600763541991L,isaacRandom.nextLong());
}
```
## Case 3.3: org.apache.commons.math3.distribution.AbstractRealDistribution:reseedRandomGenerat
```java
243	 	    /** {@inheritDoc} */
244	 	    public void reseedRandomGenerator(long seed) {
245	1	        random.setSeed(seed);
246	1	        randomData.reSeed(seed);
247	 	    }

245		1. removed call to org/apache/commons/math3/random/RandomGenerator::setSeed → KILLED
246		1. removed call to org/apache/commons/math3/random/RandomDataImpl::reSeed → SURVIVED
```
### Code refactoring:
#### Attempt 1:  
test_distance > 5 → add direct test → work!!!
```java
@Test
public void testReseedRandomGenerator(){
    RandomGenerator generator = new Well44497b();
    AbstractRealDistribution abstractRealDistribution = new AbstractRealDistribution(generator){

        public double density(double x) {
            return 0;
        }

        public double cumulativeProbability(double x) {
            return 0;
        }

        public double getNumericalMean() {
            return 0;
        }

        public double getNumericalVariance() {
            return 0;
        }

        public double getSupportLowerBound() {
            return 0;
        }

        public double getSupportUpperBound() {
            return 0;
        }

        public boolean isSupportLowerBoundInclusive() {
            return false;
        }

        public boolean isSupportUpperBoundInclusive() {
            return false;
        }

        public boolean isSupportConnected() {
            return false;
        }
    };

    abstractRealDistribution.reseedRandomGenerator(123456789012345678L);
    assertEquals(123456789012345678L,
            abstractRealDistribution.randomData.nextLong(
                    123456789012345677L,123456789012345678L));
}
```
## Case 3.4:
org.apache.commons.math3.random.RandomAdaptor:setSeed		
```java
189	 	    /** {@inheritDoc} */
190	 	    @Override
191	 	    public void setSeed(long seed) {
192	1	        if (randomGenerator != null) {  // required to avoid NPE in constructor
193	1	            randomGenerator.setSeed(seed);
194	 	        }
195	 	    }

192		1. negated conditional → KILLED
193		1. removed call to org/apache/commons/math3/random/RandomGenerator::setSeed → SURVIVED
```
### Code refactoring:
#### Attempt 1:  
test_distance > 5 → add direct test → work!!!
```java
@Test
public void testSetSeed(){
    RandomGenerator generator = new Well44497b();
    RandomAdaptor randomAdaptor = new RandomAdaptor(generator);
    long seed = 123456789L;
    randomAdaptor.setSeed(seed);
    assertEquals(-930160139684413525L,randomAdaptor.nextLong());
}
```
# Rule 4:
test_distance > 5 && (loop(loop)) <= 0 && is_nested = 0 && is_public = 1 && NOCL > 4 && (cond) <= 0 && is_static = 0 && LMET <= 1 && NOCL > 8 && NOPR > 5 && is_void = 1

## Case 4.1: org.jfree.chart.renderer.category.AbstractCategoryItemRenderer:drawOutline		
```java
808	 	    /**
809	 	     * Draws an outline for the data area.  The default implementation just
810	 	     * gets the plot to draw the outline, but some renderers will override this
811	 	     * behaviour.
812	 	     *
813	 	     * @param g2  the graphics device.
814	 	     * @param plot  the plot.
815	 	     * @param dataArea  the data area.
816	 	     */
817	 	    @Override
818	 	    public void drawOutline(Graphics2D g2, CategoryPlot plot,
819	 	            Rectangle2D dataArea) {
820	1	        plot.drawOutline(g2, dataArea);
821	 	    }

820		1. removed call to org/jfree/chart/plot/CategoryPlot::drawOutline → SURVIVED
```
### Code refactoring:
#### Attempt 1:  
test_distance > 5 → add direct test → works!!!
```java
@Test
public void testDrawOutline(){
    AbstractCategoryItemRenderer r = new LineAndShapeRenderer();
    BufferedImage image = new BufferedImage(200 , 100,
            BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = image.createGraphics();
    CategoryPlot plot = new CategoryPlot();
    Rectangle2D dataArea = new Rectangle2D.Double();
    r.drawOutline(g2,plot,dataArea);
    assertTrue(g2.getStroke()==plot.getOutlineStroke());
}
```
## Case 4.2: org.jfree.chart.plot.XYPlot:zoomDomainAxes		
```java
5048	 	    /**
5049	 	     * Multiplies the range on the domain axis/axes by the specified factor.
5050	 	     *
5051	 	     * @param factor  the zoom factor.
5052	 	     * @param info  the plot rendering info.
5053	 	     * @param source  the source point (in Java2D space).
5054	 	     *
5055	 	     * @see #zoomRangeAxes(double, PlotRenderingInfo, Point2D)
5056	 	     */
5057	 	    @Override
5058	 	    public void zoomDomainAxes(double factor, PlotRenderingInfo info,
5059	 	                               Point2D source) {
5060	 	        // delegate to other method
5061	1	        zoomDomainAxes(factor, info, source, false);
5062	 	    }

5061		1. removed call to org/jfree/chart/plot/XYPlot::zoomDomainAxes → SURVIVED
```
### Code refactoring:
#### Attempt 1:  
test_distance > 5 → add direct test → works!!!
```java
@Test
public void testZoomDomainAxes(){
    XYSeriesCollection dataset = new XYSeriesCollection();
    NumberAxis xAxis = new NumberAxis("X");
    NumberAxis yAxis = new NumberAxis("Y");
    XYItemRenderer renderer = new DefaultXYItemRenderer();
    XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
    Point2D zp = new Point();
    ChartRenderingInfo owner = new ChartRenderingInfo();
    PlotRenderingInfo info = new PlotRenderingInfo(owner);
    assertEquals("Range[0.0,1.05]" , plot.getDomainAxis().getRange().toString());
    plot.zoomDomainAxes(2.0,info,zp);
    assertEquals("Range[-0.525,1.5750000000000002]",plot.getDomainAxis().getRange().toString());
}
```
##Case 4.4: org.jfree.chart.renderer.category.AbstractCategoryItemRenderer:setSeriesItemURLGenerator		
```java
563	 	    /**
564	 	     * Sets the URL generator for a series and sends a
565	 	     * {@link RendererChangeEvent} to all registered listeners.
566	 	     *
567	 	     * @param series  the series index (zero based).
568	 	     * @param generator  the generator.
569	 	     * @param notify  notify listeners?
570	 	     *
571	 	     * @see #getSeriesItemURLGenerator(int)
572	 	     */
573	 	    @Override
574	 	    public void setSeriesItemURLGenerator(int series,
575	 	            CategoryURLGenerator generator, boolean notify) {
576	 	        this.itemURLGeneratorMap.put(series, generator);
577	1	        if (notify) {
578	1	            fireChangeEvent();
579	 	        }
580	 	    }

577		1. negated conditional → SURVIVED
578		1. removed call to org/jfree/chart/renderer/category/AbstractCategoryItemRenderer::fireChangeEvent → SURVIVED
```
### Code refactoring:
#### Attempt 1:  
test_distance > 5 → add direct test → not work because there is no way to examine the change of fireChangeEvent() method call.
```java
@Test
public void testSetSeriesItemURLGenerator0(){
    AbstractCategoryItemRenderer r;
    //case 1
    r = new BarRenderer();
    assertTrue(r.getSeriesItemLabelGenerator(0)==null);
    r.setSeriesItemLabelGenerator(0,
            new IntervalCategoryItemLabelGenerator(),false);
    assertTrue(r.getSeriesItemLabelGenerator(0)!=null);

    // case 2
    r = new BarRenderer();
    assertTrue(r.getSeriesItemLabelGenerator(0)==null);
    r.setSeriesItemLabelGenerator(0,
            new IntervalCategoryItemLabelGenerator(),true);
    assertTrue(r.getSeriesItemLabelGenerator(0)!=null);
}
```
#### Attempt 2:  
is_void = 1 → The changing of void to a return value does not work, since fireChangeEvent() is also void method and no changes to any field in its class org.jfree.chart.renderer.AbstractRenderer. → code refactoring: add an private field notifiedNo to count the method call to fireChangeEvent() in org.jfree.chart.renderer.AbstractRenderer and a getter to this field. → works!!!

In class org.jfree.chart.renderer.AbstractRenderer:
```java
private int notifiedNo = 0;
public int getNotifiedNo(){
    return  notifiedNo;
}

@Test
public void testSetSeriesItemURLGenerator(){
    AbstractCategoryItemRenderer r;
    //case 1
    r = new BarRenderer();
    int currentNotifiedNo = r.getNotifiedNo();
    r.setSeriesItemLabelGenerator(0,
            new IntervalCategoryItemLabelGenerator(),false);
    int afterNotifiedNo = r.getNotifiedNo();
    assertTrue(currentNotifiedNo==afterNotifiedNo);

    // case 2
    r = new BarRenderer();
    int currentNotifiedNo2 = r.getNotifiedNo();
    r.setSeriesItemLabelGenerator(0,
            new IntervalCategoryItemLabelGenerator(),true);
    int afterNotifiedNo2 = r.getNotifiedNo();
    assertTrue(currentNotifiedNo2+1==afterNotifiedNo2);
}
```

# Rule 5:
test_distance <= 5 && is_void = 1 && nested_depth <= 0 && NOS <= 2 && assertion-density <= 0.14 && MOD > 1

## Case 5.1: org.apache.commons.lang3.builder.StandardToStringStyle:setUseShortClassName
```java
81	 	    /**
82	 	     * <p>Sets whether to output short or long class names.</p>
83	 	     *
84	 	     * @param useShortClassName  the new useShortClassName flag
85	 	     * @since 2.0
86	 	     */
87	 	    @Override
88	 	    public void setUseShortClassName(final boolean useShortClassName) { // NOPMD as this is implementing the abstract class
89	1	        super.setUseShortClassName(useShortClassName);
90	 	    }

89		1. removed call to org/apache/commons/lang3/builder/ToStringStyle::setUseShortClassName → SURVIVED
```
### Code refactoring:
#### Attempt 1:  
assertion-density <= 0.14 → although this method is invoked in the tests, but no proper assertion statements are used to examine the changes. → add assertions → works!!!
```java
@Test
public void testSetUseShortClassName(){
    assertTrue(STYLE.isUseShortClassName());
    STYLE.setUseShortClassName(false);
    assertFalse(STYLE.isUseShortClassName());
    STYLE.setUseShortClassName(true);
    assertTrue(STYLE.isUseShortClassName());
}
```
## Case 5.2: org.apache.commons.lang3.builder.StandardToStringStyle:setSummaryObjectStartText
```java
510	 	    /**
511	 	     * <p>Sets the start text to output when an <code>Object</code> is
512	 	     * output in summary mode.</p>
513	 	     *
514	 	     * <p>This is output before the size value.</p>
515	 	     *
516	 	     * <p><code>null</code> is accepted, but will be converted to
517	 	     * an empty String.</p>
518	 	     *
519	 	     * @param summaryObjectStartText  the new start of summary text
520	 	     */
521	 	    @Override
522	 	    public void setSummaryObjectStartText(final String summaryObjectStartText) { // NOPMD as this is implementing the abstract class
523	1	        super.setSummaryObjectStartText(summaryObjectStartText);
524	 	    }

523		1. removed call to org/apache/commons/lang3/builder/ToStringStyle::setSummaryObjectStartText → SURVIVED
```
### Code refactoring:
#### Attempt 1:  
assertion-density <= 0.14 → although this method is invoked in the tests, but no proper assertion statements are used to examine the changes. → add assertions → works!!!
```java
@Test
public void testSetSummaryObjectStartText(){
    assertEquals("%",STYLE.getSummaryObjectStartText());
    STYLE.setSummaryObjectStartText("&");
    assertEquals("&",STYLE.getSummaryObjectStartText());
    STYLE.setSummaryObjectStartText("%");
}
```
# Rule 6:
test_distance <= 5 && is_void = 1 && nested_depth <= 0 && NOS > 2 && assertion-density <= 0.22 && CREF > 1 && XMET > 0 && VDEC <= 0 && NOCL <= 12

## Case 6.1: org.jfree.chart.LegendItem:setLinePaint		
```java
873	 	    /**
874	 	     * Sets the line paint.
875	 	     *
876	 	     * @param paint  the paint ({@code null} not permitted).
877	 	     *
878	 	     * @since 1.0.11
879	 	     */
880	 	    public void setLinePaint(Paint paint) {
881	1	        Args.nullNotPermitted(paint, "paint");
882	 	        this.linePaint = paint;
883	 	    }

881		1. removed call to org/jfree/chart/util/Args::nullNotPermitted → SURVIVED
```
### Code refactoring:
#### Attempt 1:  
assertion-density <= 0.22→ although this method is invoked in the tests, but no proper assertion statements are used to examine the changes of Args.nullNotPermitted() which is used to check paint object is not null. → add additional test and assertions to trigger IllegalArgumentException → works!!!
```java
@Test (expected = IllegalArgumentException.class)
public void testSetLinePaintNull(){
    LegendItem item = new LegendItem("Item");
    item.setLinePaint(null);
}
```
## Case 6.2: org.apache.commons.math3.exception.TooManyEvaluationsException:<init>		
```java
30	 	    /**
31	 	     * Construct the exception.
32	 	     *
33	 	     * @param max Maximum number of evaluations.
34	 	     */
35	 	    public TooManyEvaluationsException(Number max) {
36	 	        super(max);
37	1	        getContext().addMessage(LocalizedFormats.EVALUATIONS);
38	 	    }

37		1. removed call to org/apache/commons/math3/exception/util/ExceptionContext::addMessage → SURVIVED
```

### Code refactoring:
#### Attempt 1:  
is_void = 1→ Mutant in Line 37 cannot be examined because List<Localizable> msgPatterns is a private field in ExceptionContext. So the first step is to add a getter for msgPatterns.
```java
public List<Localizable> getMsgPatterns(){
    return msgPatterns;
}
```
#### Attempt 2:  
assertion-density <= 0.22→ add additional assertions to examines the changes in msgPatterns. → works!!!
```java
@Test
public void testMessage() {
    final int max = 12345;
    final TooManyEvaluationsException e = new TooManyEvaluationsException(max);
    final String msg = e.getLocalizedMessage();
    Assert.assertTrue(msg,
                      msg.matches(".*?" +
                                  MessageFormat.format("{0}", max) +
                                  ".*"));                  Assert.assertTrue(e.getContext().getMsgPatterns().contains(LocalizedFormats.EVALUATIONS));
}
```
