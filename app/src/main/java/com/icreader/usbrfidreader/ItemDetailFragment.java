package com.icreader.usbrfidreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.icreader.usbrfidreader.dummy.DummyContent;
import com.icreader.usbrfidreader.usbrfidreader.ICReaderApi;

/**
 * A fragment representing a single Item detail screen. This fragment is either
 * contained in a {@link ItemListActivity} in two-pane mode (on tablets) or a
 * {@link ItemDetailActivity} on handsets.
 */
public class ItemDetailFragment extends PreferenceFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;

	private ICReaderApi api = null;
	private Context context = null;
	SharedPreferences setting = null;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ItemDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mItem = DummyContent.ITEM_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
			if (mItem.id.equals("1")) {
				addPreferencesFromResource(R.xml.pref_system_setting);
				SystemSettingFunc();
			}
			if (mItem.id.equals("2")) {
				addPreferencesFromResource(R.xml.pref_mf);
				Mf_Func();
			}
			if (mItem.id.equals("3")) {
				addPreferencesFromResource(R.xml.pref_ultralight);
				Ult_Func();
			}
			if (mItem.id.equals("4")) {
				addPreferencesFromResource(R.xml.pref_14443b);
				_14443B_Func();
			}
			if (mItem.id.equals("5")) {
				addPreferencesFromResource(R.xml.pref_15693);
				_15693_Func();
			}
		}
		context = this.getActivity();
		UsbDevice device = (UsbDevice) getArguments().getParcelable("device");
		if (device == null) {
			Toast.makeText(context, "你打开的方式不对！", Toast.LENGTH_LONG).show();
			return;
		}
		UsbManager manager = (UsbManager) context
				.getSystemService(context.USB_SERVICE);
		api = new ICReaderApi(device, manager);
		setting = this.getPreferenceManager().getSharedPreferences();
	}

	void showDialog(String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(text);
		builder.setTitle("结果");
		builder.setPositiveButton("确认", null);
		builder.create().show();
	}

	private String showData(String text, byte[] data, String str, int pos,
							int len) {
		String dStr = "";
		for (int i = 0; i < len; i++) {
			dStr += String.format("%02x ", data[i + pos]);
		}
		text += (str + dStr.toUpperCase() + '\n');
		return text;
	}

	private String showData(String text, byte[] data, String str) {
		String dStr = "";
		for (byte b : data) {
			dStr += String.format("%02x ", b);
		}
		text += (str + dStr.toUpperCase() + '\n');
		return text;
	}

	private byte[] getByteArray(String str) {
		str = str.replaceAll("[^0-9A-F]", "");
		byte[] ans = new byte[str.length() / 2];
		for (int i = 0; i < str.length(); i += 2) {
			ans[i / 2] = (byte) Integer.parseInt(str.substring(i, i + 2), 16);
		}
		return ans;
	}

	private String showStatue(String text, int Code) {
		String msg = null;
		switch (Code) {
			case 0x00:
				msg = "命令执行成功 .....";
				break;
			case 0x01:
				msg = "命令操作失败 .....";
				break;
			case 0x02:
				msg = "地址校验错误 .....";
				break;
			case 0x03:
				msg = "没有选择COM口 .....";
				break;
			case 0x04:
				msg = "读写器返回超时 .....";
				break;
			case 0x05:
				msg = "数据包流水号不正确 .....";
				break;
			case 0x07:
				msg = "接收异常 .....";
				break;
			case 0x0A:
				msg = "参数值超出范围 .....";
				break;
			case 0x80:
				msg = "参数设置成功 .....";
				break;
			case 0x81:
				msg = "参数设置失败 .....";
				break;
			case 0x82:
				msg = "通讯超时.....";
				break;
			case 0x83:
				msg = "卡不存在.....";
				break;
			case 0x84:
				msg = "接收卡数据出错.....";
				break;
			case 0x85:
				msg = "未知的错误.....";
				break;
			case 0x87:
				msg = "输入参数或者输入命令格式错误.....";
				break;
			case 0x89:
				msg = "输入的指令代码不存在.....";
				break;
			case 0x8A:
				msg = "在对于卡块初始化命令中出现错误.....";
				break;
			case 0x8B:
				msg = "在防冲突过程中得到错误的序列号.....";
				break;
			case 0x8C:
				msg = "密码认证没通过.....";
				break;
			case 0x8F:
				msg = "输入的指令代码不存在.....";
				break;
			case 0x90:
				msg = "卡不支持这个命令.....";
				break;
			case 0x91:
				msg = "命令格式有错误.....";
				break;
			case 0x92:
				msg = "在命令的FLAG参数中，不支持OPTION 模式.....";
				break;
			case 0x93:
				msg = "要操作的BLOCK不存在.....";
				break;
			case 0x94:
				msg = "要操作的对象已经别锁定，不能进行修改.....";
				break;
			case 0x95:
				msg = "锁定操作不成功.....";
				break;
			case 0x96:
				msg = "写操作不成功.....";
				break;
		}
		msg += '\n';
		text += msg;
		return text;
	}

	void SystemSettingFunc() {
		findPreference("sys_set_num").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						byte[] buffer = new byte[1];
						buffer[0] = 0;
						byte[] newValue = getByteArray(setting.getString(
								"sys_num", "AA BB AA BB AA BB AA BB"));
						int result = api.API_SetSerNum(newValue, buffer);
						String text = new String();
						text = showStatue(text, result);
						text = showStatue(text, buffer[0]);
						showDialog(text);
						return false;
					}

				});
		findPreference("sys_get_num").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						byte[] buffer = new byte[9];
						int result = api.API_GetSerNum(buffer);
						String text = new String();
						text = showStatue(text, result);
						text = showData(text, buffer, "序列号: \n", 1, 8);
						showDialog(text);
						return false;
					}

				});

		findPreference("sys_con_led").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						byte freq = (byte) Integer.parseInt(
								setting.getString("sys_con_circle", "4"), 16);
						byte duration = (byte) Integer.parseInt(
								setting.getString("sys_con_time", "18"), 16);
						byte[] buffer = new byte[1];
						int result = api.API_ControlLED(freq, duration, buffer);
						String text = new String();
						text = showStatue(text, result);
						text = showStatue(text, buffer[0]);
						showDialog(text);
						return false;
					}

				});

		findPreference("sys_con_buzzer").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte freq = (byte) Integer.parseInt(
								setting.getString("sys_con_circle", "4"), 16);
						byte duration = (byte) Integer.parseInt(
								setting.getString("sys_con_time", "18"), 16);
						byte[] buffer = new byte[1];
						int result = api.API_ControlBuzzer(freq, duration,
								buffer);
						text = showStatue(text, result);
						text = showStatue(text, buffer[0]);
						showDialog(text);
						return false;
					}

				});
		findPreference("sys_get_ver").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte[] VersionNum = new byte[256]; // version number
						int result = api.API_GetSerNum(VersionNum);
						text = showStatue(text, result);
						if (result == 0)
							text = showData(text, VersionNum, "版本号: ");
						else
							text = showStatue(text, VersionNum[0]);
						showDialog(text);
						return false;
					}

				});
	}

	void Mf_Func() {
		findPreference("mf_read_card").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte mode1 = (byte) (setting.getString("mf_set_key",
								"Key A").equals("Key A") ? 0 : 1);
						byte mode2 = (byte) (setting.getString("mf_set_ai",
								"Idle").equals("Idle") ? 0 : 1);
						byte mode = (byte) ((mode1 << 1) | mode2); // reading
						// model
						byte blk_add = (byte) Integer.parseInt(
								setting.getString("mf_add", "10"), 16); // block
						// address
						byte num_blk = (byte) Integer.parseInt(
								setting.getString("mf_num", "04"), 16); // block
						// number
						byte[] snr = getByteArray(setting.getString(
								"mf_set_pas", "FF FF FF FF FF FF")); // key
						byte[] buffer = new byte[16 * num_blk]; // data read

						int result = api.API_PCDRead(mode, blk_add, num_blk,
								snr, buffer);

						text = showStatue(text, result);
						if (result == 0) {
							text = showData(text, snr, "卡号:\n", 0,
									4);
							text = showData(text, buffer, "卡数据:\n",
									0, 16 * num_blk);
						} else
							text = showStatue(text, snr[0]);
						showDialog(text);
						return false;
					}
				});
		findPreference("mf_write_card").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte mode1 = (byte) (setting.getString("mf_set_key",
								"Key A").equals("Key A") ? 0 : 1);
						byte mode2 = (byte) (setting.getString("mf_set_ai",
								"Idle").equals("Idle") ? 0 : 1);
						byte mode = (byte) ((mode1 << 1) | mode2); // reading
						// model
						byte blk_add = (byte) Integer.parseInt(
								setting.getString("mf_add", "10"), 16); // block
						// address
						byte num_blk = (byte) Integer.parseInt(
								setting.getString("mf_num", "01"), 16); // block
						// number
						byte[] snr = getByteArray(setting.getString(
								"mf_set_pas", "FF FF FF FF FF FF")); // key
						byte[] buffer = getByteArray(setting
								.getString("mf_data",
										"FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF FF"));
						int result = api.API_PCDWrite(mode, blk_add, num_blk,
								snr, buffer);

						text = showStatue(text, result);
						if (result == 0) {
							text = showData(text, snr, "卡号:\n", 0,
									4);
						} else
							text = showStatue(text, snr[0]);
						showDialog(text);
						return false;
					}
				});
		findPreference("mf_init").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte mode1 = (byte) (setting.getString("mf_set_key",
								"Key A").equals("Key A") ? 0 : 1);
						byte mode2 = (byte) (setting.getString("mf_set_ai",
								"Idle").equals("Idle") ? 0 : 1);
						byte mode = (byte) ((mode1 << 1) | mode2); // reading
						// model
						byte SectNum = (byte) Integer.parseInt(
								setting.getString("mf_init_block", "01"), 16); // sector
						byte[] snr = getByteArray(setting.getString(
								"mf_set_pas", "FF FF FF FF FF FF")); // key
						byte[] value = getByteArray(setting.getString(
								"mf_init_data", "00 88 00 00")); // initialize
						// value

						int result = api.API_PCDInitVal(mode, SectNum, snr,
								value);

						text = showStatue(text, result);
						if (result == 0) {
							text = showData(text, snr, "卡号:\n", 0,
									4);
						} else
							text = showStatue(text, snr[0]);
						showDialog(text);
						return false;
					}
				});
		findPreference("mf_dec").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte mode1 = (byte) (setting.getString("mf_set_key",
								"Key A").equals("Key A") ? 0 : 1);
						byte mode2 = (byte) (setting.getString("mf_set_ai",
								"Idle").equals("Idle") ? 0 : 1);
						byte mode = (byte) ((mode1 << 1) | mode2); // reading
						// model
						byte SectNum = (byte) Integer.parseInt(
								setting.getString("mf_init_block", "01"), 16); // sector
						byte[] snr = getByteArray(setting.getString(
								"mf_set_pas", "FF FF FF FF FF FF")); // key
						byte[] value = getByteArray(setting.getString(
								"mf_init_data", "00 88 00 00")); // initialize
						// value
						int result = api.API_PCDDec(mode, SectNum, snr, value);

						text = showStatue(text, result);
						if (result == 0) {
							text = showData(text, snr, "卡号:\n", 0,
									4);
						} else
							text = showStatue(text, snr[0]);
						showDialog(text);
						return false;
					}
				});
		findPreference("mf_inc").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte mode1 = (byte) (setting.getString("mf_set_key",
								"Key A").equals("Key A") ? 0 : 1);
						byte mode2 = (byte) (setting.getString("mf_set_ai",
								"Idle").equals("Idle") ? 0 : 1);
						byte mode = (byte) ((mode1 << 1) | mode2); // reading
						// model
						byte SectNum = (byte) Integer.parseInt(
								setting.getString("mf_init_block", "01"), 16); // sector
						byte[] snr = getByteArray(setting.getString(
								"mf_set_pas", "FF FF FF FF FF FF")); // key
						byte[] value = getByteArray(setting.getString(
								"mf_init_data", "00 88 00 00")); // initialize
						// value
						int result = api.API_PCDInc(mode, SectNum, snr, value);

						text = showStatue(text, result);
						if (result == 0) {
							text = showData(text, snr, "卡号:\n", 0,
									4);
						} else
							text = showStatue(text, snr[0]);
						showDialog(text);
						return false;
					}
				});
		findPreference("mf_read_num").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte mode;
						if (!setting.getString("mf_set_ai", "Idle").equals(
								"Idle"))
							mode = 0x52;
						else
							mode = 0x26;
						byte halt = (byte) 0;
						byte[] snr = new byte[1];
						byte[] value = new byte[5]; // card number
						int result = api.GET_SNR(mode, halt, snr, value);
						text = showStatue(text, result);
						if (result == 0) {
							if (snr[0] == 0x00)
								text += ("只有一张卡.....\n");
							else
								text += ("多于一张卡......\n");
							text = showData(text, value, "卡号:\n",
									0, 4);
						} else
							text = showStatue(text, snr[0]);
						showDialog(text);
						return false;
					}
				});
	}

	void Ult_Func() {
		findPreference("ult_read_card").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte[] snr = new byte[7];
						byte mode = (byte) (setting.getString("utl_set_ai",
								"Idle").equals("Idle") ? 0 : 1);
						int result = api.UL_Request(mode, snr);
						text = showStatue(text, result);
						if (result == 0) {
							text = showData(text, snr, "卡号:\n", 0,
									7);
						} else
							text = showStatue(text, snr[0]);
						showDialog(text);
						return false;
					}
				});
		findPreference("utl_halt").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						int result = api.MF_Halt();
						text = showStatue(text, result);
						showDialog(text);
						return false;
					}
				});
		findPreference("ult_read").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte mode = (byte) (setting.getString("utl_set_ai",
								"Idle").equals("Idle") ? 0 : 1);
						byte blk_add = (byte) Integer.parseInt(setting
								.getString("ult_sec", "04"));
						byte[] snr = new byte[7];
						byte[] buffer = new byte[16];
						int result = api.UL_HLRead(mode, blk_add, snr, buffer);
						text = showStatue(text, result);
						if (result == 0) {
							text = showData(text, snr, "卡号:\n", 0,
									7);
							text = showData(text, buffer, "卡数据:\n",
									0, 16);
						} else
							text = showStatue(text, buffer[0]);
						showDialog(text);
						return false;
					}
				});
		findPreference("ult_write").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte mode = (byte) (setting.getString("utl_set_ai",
								"Idle").equals("Idle") ? 0 : 1);
						byte blk_add = (byte) Integer.parseInt(setting
								.getString("ult_sec", "04"));
						byte[] snr = new byte[7];
						byte[] buffer = getByteArray(setting.getString(
								"ult_data", "00 00 00 00"));
						int result = api.UL_HLWrite(mode, blk_add, snr, buffer);
						if (result == 0) {
							text = showStatue(text, result);
							text = showData(text, snr, "卡号:\n", 0,
									7);
						} else if (result == 10) {
							text += ("错误\n");
							text = showStatue(text, result);
						}// something different
						else
							text = showStatue(text, snr[0]);
						showDialog(text);
						return false;
					}
				});

	}

	void _14443B_Func() {
		findPreference("14443b_req").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte[] buffer = new byte[300];
						int result = api.RequestType_B(buffer);
						text = showStatue(text, result);
						if (result == 0) {
							text = showData(text, buffer, "数据长度:\n",
									0, 1);
							text = showData(text, buffer, "数据:\n", 1,
									buffer[0]);
						} else
							text = showStatue(text, buffer[0]);
						showDialog(text);
						return false;
					}
				});
		findPreference("14443b_send").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						int cmdSize = Integer.parseInt(
								setting.getString("14443b_data_len", "08"), 16);
						byte[] buffer = new byte[256];
						byte[] cmd = getByteArray(setting.getString(
								"14443b_data", "00 00 05 00 84 00 00 08"));
						int result = api.API_ISO14443TypeBTransCOSCmd(cmd,
								cmdSize, buffer);
						text = showStatue(text, result);
						if (result == 0) {
							text = showData(text, buffer, "数据:\n", 0, 8);
						} else
							text = showStatue(text, buffer[0]);
						showDialog(text);
						return false;
					}
				});
	}

	void _15693_Func() {
		findPreference("15693_read").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte flags = (byte) Integer.parseInt(
								setting.getString("15693_flag", "02"), 16);
						byte blk_add = (byte) Integer.parseInt(
								setting.getString("15693_add", "05"), 16);
						byte num_blk = (byte) Integer.parseInt(
								setting.getString("15693_num", "01"), 16);
						byte[] uid = getByteArray(setting.getString(
								"15693_uid", ""));
						byte[] buffer = new byte[256];
						int n;
						if (flags == 0x42)
							n = 5;
						else
							n = 4;
						int result = api.API_ISO15693Read(flags, blk_add,
								num_blk, uid, buffer);
						text = showStatue(text, result);
						if (result == 0) {
							text = showData(text, buffer, "标志位:\n", 0, 1);
							text = showData(text, buffer, "卡数据:\n",
									1, n * num_blk);
						} else
							text = showStatue(text, buffer[0]);
						showDialog(text);
						return false;
					}
				});
		findPreference("15693_write").setOnPreferenceClickListener(
				new OnPreferenceClickListener() {
					@Override
					public boolean onPreferenceClick(Preference arg0) {
						// TODO Auto-generated method stub
						String text = new String();
						byte flags = (byte) Integer.parseInt(
								setting.getString("15693_flag", "02"), 16);
						byte blk_add = (byte) Integer.parseInt(
								setting.getString("15693_add", "05"), 16);
						byte num_blk = (byte) Integer.parseInt(
								setting.getString("15693_num", "01"), 16);
						byte[] uid = getByteArray(setting.getString(
								"15693_uid", ""));
						byte[] data = getByteArray(setting.getString(
								"15693_data", "11 22 33 44"));
						int n;
						if (flags == 0x42)
							n = 5;
						else
							n = 4;
						int result = api.API_ISO15693Write(flags, blk_add,
								num_blk, uid, data);
						text = showStatue(text, result);
						if (result != 0)
							text = showStatue(text, data[0]);
						showDialog(text);
						return false;
					}
				});
	}
}
