package printproject.com.printproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.mocoo.hang.rtprinter.driver.BitmapConvertUtil;
import com.mocoo.hang.rtprinter.driver.HsBluetoothPrintDriver;

import java.io.IOException;

import printproject.com.model.SalesModel;
import printproject.com.utility.Utility;

/**
 * This class is responsible to generate a static sales receipt and to print that receipt
 */
public class PrintReceipt {

	public static boolean  printBillFromOrder(Context context){
		if(BluetoothPrinterActivity.BLUETOOTH_PRINTER.IsNoConnection()){
			return false;
		}
	
		double totalBill=0.00, netBill=0.00, totalVat=0.00;

		//LF = Line feed
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.Begin();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//CENTER
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//30 * 0.125mm
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write("iNPRINT Corp.");
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);

        //BT_Write() method will initiate the printer to start printing.
        BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write("Branch Name: " + "iNPRINT Siam Square 3" +
				"\nOrder No: " + "1245784256454" +
				"\nBill No: " + "554741254854" +
				"\nTrn. Date:" + "29/12/1988" +
				"\nSalesman:" + "Mr. Sale GoodJob");
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//LEFT
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//50 * 0.125mm
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal font

		//static sales record are generated
		SalesModel.generatedMoneyReceipt();

		for(int i=0;i<StaticValue.arrayListSalesModel.size();i++){
			SalesModel salesModel = StaticValue.arrayListSalesModel.get(i);
			BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write(salesModel.getProductShortName());
			BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
			BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write(" " + salesModel.getSalesAmount() + "x" + salesModel.getUnitSalesCost() +
					"=" + Utility.doubleFormatter(salesModel.getSalesAmount() * salesModel.getUnitSalesCost()) + "" + StaticValue.CURRENCY);
			BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
			
			totalBill=totalBill + (salesModel.getUnitSalesCost() * salesModel.getSalesAmount());
		}
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));
		
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//RIGHT
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);	//50 * 0.125mm
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte)0x00);//normal font
		
		totalVat=Double.parseDouble(Utility.doubleFormatter(totalBill*(StaticValue.VAT/100)));
		netBill=totalBill+totalVat;
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write("Total Bill:" + Utility.doubleFormatter(totalBill) + "" + StaticValue.CURRENCY);
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write(Double.toString(StaticValue.VAT) + "% VAT:" + Utility.doubleFormatter(totalVat) + "" +
				StaticValue.CURRENCY);
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));

		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetLineSpacing((byte) 30);
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 2);//Right
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x9);
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write("Net Bill:" + Utility.doubleFormatter(netBill) + "" + StaticValue.CURRENCY);
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 1);//center
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetFontEnlarge((byte) 0x00);//normal font
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write(context.getResources().getString(R.string.print_line));
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//left
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write("VAT Reg. No:" + StaticValue.VAT_REGISTRATION_NUMBER);
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte) 0);//left
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.BT_Write(StaticValue.BRANCH_ADDRESS);
		
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.SetAlignMode((byte)1);//Center


		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		BluetoothPrinterActivity.BLUETOOTH_PRINTER.LF();
		return true;
	}

	public static boolean PrintImage(HsBluetoothPrintDriver printer, Intent intent, Context context) throws IOException {


		Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
		if(uri != null){
			Bitmap bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);

			int maxWidth = 550;


			if(!printer.IsNoConnection()){
				bmp = BitmapConvertUtil.scaleToRequiredWidth(bmp, maxWidth);

				Bitmap newBm = BitmapConvertUtil.decodeSampledBitmapFromBitmap(bmp, maxWidth);


				byte xL = (byte)(((newBm.getWidth() - 1) / 8 + 1) % 256);
				byte xH = (byte)(((newBm.getWidth() - 1) / 8 + 1) / 256);
				byte yL = (byte)(newBm.getHeight() % 256);
				byte yH = (byte)(newBm.getHeight() / 256);
				Log.d("HsBluetoothPrintDriver", "xL = " + xL);
				Log.d("HsBluetoothPrintDriver", "xH = " + xH);
				Log.d("HsBluetoothPrintDriver", "yL = " + yL);
				Log.d("HsBluetoothPrintDriver", "yH = " + yH);
				byte[] pixels = BitmapConvertUtil.convert(newBm);
				printer.BT_Write(new byte[]{29, 118, 48, 0, xL, xH, yL, yH});
				printer.BT_Write(pixels);
				printer.BT_Write(new byte[]{10});
				printer.stop();

				return true;
			}


		}




		return false;
	}
}
