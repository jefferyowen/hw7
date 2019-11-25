import org.junit.Test;

import java.util.ArrayList;

import edu.cs3500.spreadsheets.model.Cell;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.WorkSheet;
import edu.cs3500.spreadsheets.model.WorkSheetBasic;
import edu.cs3500.spreadsheets.model.cells.CellComponentBlank;
import edu.cs3500.spreadsheets.model.cells.formula.CellComponentFormulaReference;
import edu.cs3500.spreadsheets.model.cells.formula.value.CellComponentValueBoolean;
import edu.cs3500.spreadsheets.model.cells.formula.value.CellComponentValueDouble;
import edu.cs3500.spreadsheets.model.cells.formula.value.CellComponentValueString;
import edu.cs3500.spreadsheets.model.cells.functions.FunctionLessThan;
import edu.cs3500.spreadsheets.model.cells.functions.FunctionProduct;
import edu.cs3500.spreadsheets.model.cells.functions.FunctionRepeat;
import edu.cs3500.spreadsheets.model.cells.functions.FunctionSum;

import static org.junit.Assert.assertEquals;

/**
 * Tester class for HW 5.
 */
public class Hw5Checks {

  @Test
  public void testEmptySpreadSheet() {
    WorkSheet ws = new WorkSheetBasic(1, 1);
    assertEquals("", ws.getCellAt(0, 0).toString());
  }


  @Test
  public void testAddCells() {

    WorkSheet ws = new WorkSheetBasic(4, 4);
    assertEquals("", ws.getCellAt(1, 1).toString());
    ws.addCell(1, 1);
    ws.setCell(1, 1, new CellComponentValueString("tiger"));
    assertEquals("\"tiger\"", ws.getCellAt(1, 1).toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStopsCyclical() {

    WorkSheetBasic ws = new WorkSheetBasic(4, 4);
    ws.addCell(1, 1);
    ArrayList<Coord> cs = new ArrayList<Coord>();
    cs.add(new Coord(1, 1));
    ws.setCell(1, 1, new CellComponentFormulaReference(cs,
            new Coord(1, 1), ws));
    assertEquals(true, ws.getCellAt(1, 1).getCellContent().hasCycle());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testStopsCyclical2() {

    WorkSheet ws = new WorkSheetBasic(4, 4);
    ws.addCell(1, 1);
    ArrayList<Coord> cs = new ArrayList<Coord>();
    cs.add(new Coord(2, 1));
    ws.addCell(1, 2);
    ArrayList<Coord> cs2 = new ArrayList<Coord>();
    cs.add(new Coord(1, 1));
    ws.setCell(1, 1, new CellComponentFormulaReference(cs,
            new Coord(1, 1), ws));
    ws.setCell(2, 1, new CellComponentFormulaReference(cs,
            new Coord(1, 2), ws));
  }


  @Test
  public void testValueBoolean() {

    Cell c = new Cell(new CellComponentValueBoolean(false), new Coord(1, 1));
    assertEquals("false", c.getCellContent().toString());
  }

  @Test
  public void testValueString() {

    Cell c = new Cell(new CellComponentValueString("fudge"), new Coord(1, 1));
    assertEquals("\"fudge\"", c.getCellContent().toString());
  }

  @Test
  public void testValueDouble() {

    Cell c = new Cell(new CellComponentValueDouble(400.0), new Coord(1, 1));
    assertEquals("400.00", c.getCellContent().toString());
  }

  @Test
  public void testValueBlank() {

    Cell c = new Cell(new CellComponentBlank(), new Coord(1, 1));
    assertEquals("", c.getCellContent().toString());
  }

  @Test
  public void testValueFormulaRef() {

    ArrayList<Coord> cs = new ArrayList<Coord>();
    cs.add(new Coord(2, 1));
    Cell c = new Cell(new CellComponentFormulaReference(cs, new Coord(1, 1),
            new WorkSheetBasic(4, 4)), new Coord(1, 1));
    assertEquals("B1", c.getCellContent().toString());
  }


  @Test
  public void testFormulaSum() {

    WorkSheet ws = new WorkSheetBasic(2, 2);
    ws.addCell(1, 1);
    ws.addCell(1, 2);
    ws.addCell(2, 1);
    ws.addCell(2, 2);

    ArrayList<Coord> cs = new ArrayList<Coord>();
    cs.add(new Coord(1, 1));
    cs.add(new Coord(2, 1));
    cs.add(new Coord(2, 2));
    cs.add(new Coord(1, 2));

    ws.setCell(1, 1, new CellComponentValueDouble(1.0));
    ws.setCell(1, 2, new CellComponentValueDouble(2.0));
    ws.setCell(2, 1, new CellComponentValueDouble(3.0));
    ws.setCell(2, 2, new CellComponentValueDouble(4.0));

    FunctionSum fs = new FunctionSum(cs, ws, new CellComponentValueDouble(1.0));

    assertEquals((Double) 1.0, fs.evaluateFormula());
  }

  @Test
  public void testFormulaProduct() {

    WorkSheet ws = new WorkSheetBasic(2, 2);
    ws.addCell(1, 1);
    ws.addCell(1, 2);
    ws.addCell(2, 1);
    ws.addCell(2, 2);

    ArrayList<Coord> cs = new ArrayList<Coord>();
    cs.add(new Coord(1, 1));
    cs.add(new Coord(2, 1));
    cs.add(new Coord(2, 2));
    cs.add(new Coord(1, 2));

    ws.setCell(1, 1, new CellComponentValueDouble(1.0));
    ws.setCell(1, 2, new CellComponentValueDouble(2.0));
    ws.setCell(2, 1, new CellComponentValueDouble(3.0));
    ws.setCell(2, 2, new CellComponentValueDouble(4.0));

    FunctionProduct fp = new FunctionProduct(cs, ws, new CellComponentValueDouble(1.0));

    assertEquals((Double) 1.0, fp.evaluateFormula());
  }

  @Test
  public void testFormulaLessThan() {

    FunctionLessThan lt = new FunctionLessThan(new CellComponentValueDouble(10.0),
            new CellComponentValueDouble(11.0));

    assertEquals(true, lt.evaluateFormula());

    FunctionLessThan lt2 = new FunctionLessThan(new CellComponentValueDouble(24.0),
            new CellComponentValueDouble(11.0));

    assertEquals(false, lt2.evaluateFormula());
  }

  @Test
  public void testFormulaRepeat() {

    FunctionRepeat r = new FunctionRepeat(new CellComponentValueString("fudge"));

    assertEquals("fudge fudge", r.evaluateFormula());

    FunctionRepeat r2 = new FunctionRepeat(new CellComponentValueDouble(24.0));

    assertEquals("24.0 24.0", r2.evaluateFormula());
  }

  @Test
  public void testReferToSameCell() {

    WorkSheet ws = new WorkSheetBasic(2, 2);
    ws.addCell(1, 1);
    ws.addCell(1, 2);
    ws.addCell(2, 1);
    ws.addCell(2, 2);

    ArrayList<Coord> cs = new ArrayList<Coord>();
    cs.add(new Coord(1, 1));
    cs.add(new Coord(1, 1));


    ws.setCell(1, 1, new CellComponentValueDouble(1.0));
    ws.setCell(1, 2, new CellComponentValueDouble(2.0));
    ws.setCell(2, 1, new CellComponentValueDouble(3.0));
    ws.setCell(2, 2, new CellComponentValueDouble(4.0));

    FunctionSum fs = new FunctionSum(cs, ws, new CellComponentValueDouble(1.0));

    assertEquals((Double) 0.0, fs.evaluateFormula());
  }

  @Test
  public void testReferToRegionOfCells() {

    WorkSheet ws = new WorkSheetBasic(2, 2);
    ws.addCell(1, 1);
    ws.addCell(1, 2);
    ws.addCell(2, 1);
    ws.addCell(2, 2);

    ArrayList<Coord> cs = new ArrayList<Coord>();
    cs.add(new Coord(1, 1));
    cs.add(new Coord(2, 1));
    cs.add(new Coord(2, 2));
    cs.add(new Coord(1, 2));

    ws.setCell(1, 1, new CellComponentValueDouble(1.0));
    ws.setCell(1, 2, new CellComponentValueDouble(2.0));
    ws.setCell(2, 1, new CellComponentValueDouble(3.0));
    ws.setCell(2, 2, new CellComponentValueDouble(4.0));

    FunctionProduct fp = new FunctionProduct(cs, ws, new CellComponentValueDouble(1.0));

    assertEquals((Double) 1.0, fp.evaluateFormula());
  }

  @Test
  public void testFormulaWrongType() {

    WorkSheet ws = new WorkSheetBasic(2, 2);
    ws.addCell(1, 1);
    ws.addCell(1, 2);
    ws.addCell(2, 1);
    ws.addCell(2, 2);

    ArrayList<Coord> cs = new ArrayList<Coord>();
    cs.add(new Coord(1, 1));
    cs.add(new Coord(2, 1));
    cs.add(new Coord(2, 2));
    cs.add(new Coord(1, 2));

    ws.setCell(1, 1, new CellComponentValueString("snake"));
    ws.setCell(1, 2, new CellComponentValueDouble(2.0));
    ws.setCell(2, 1, new CellComponentValueDouble(3.0));
    ws.setCell(2, 2, new CellComponentValueDouble(4.0));

    FunctionProduct fp = new FunctionProduct(cs, ws, new CellComponentValueDouble(1.0));

    assertEquals((Double) 1.0, fp.evaluateFormula());
  }

  @Test
  public void testToStringWorks() {
    WorkSheet ws = new WorkSheetBasic(2, 2);
    ws.addCell(1, 1);
    ws.addCell(1, 2);
    ws.addCell(2, 1);
    ws.addCell(2, 2);


    ws.setCell(1, 1, new CellComponentValueString("snake"));
    ws.setCell(1, 2, new CellComponentValueDouble(2.0));
    ws.setCell(2, 1, new CellComponentValueBoolean(false));
    ws.setCell(2, 2, new CellComponentValueDouble(4.0));


    assertEquals("\"snake\"", ws.getCellAt(1, 1).toString());
    assertEquals(" 2.00", ws.getCellAt(1, 2).toString());
    assertEquals("false", ws.getCellAt(2, 1).toString());
    assertEquals(" 4.00", ws.getCellAt(2, 2).toString());
  }


}
