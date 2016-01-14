package com.example.mosaicimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * �Զ����ImageView���ƣ��ɶ�ͼƬ���ж�㴥�����ź��϶��ʹ���
 * 
 * @author way
 */
public class MosaicView extends View {

	/**
	 * ��ʼ��״̬����
	 */
	public static final int STATUS_INIT = 1;

	/**
	 * ͼƬ�Ŵ�״̬����
	 */
	public static final int STATUS_ZOOM_OUT_AND_MOVE = 2;

	/**
	 * ͼƬ��С״̬����
	 */
	public static final int STATUS_ZOOM_IN_AND_MOVE = 3;

	/**
	 * ͼƬ�϶�״̬����
	 */
	public static final int STATUS_MOVE = 4;

	/**
	 * ͼƬ�ֲ���ʾ״̬
	 */
	public static final int STATUS_PART = 5;

	/**
	 * ��ָ̧��ʱͼƬ�ָ�״̬
	 */
	public static final int STATUS_ACTION_UP = 6;

	/**
	 * ���Ʊʴ���С
	 */
	public static final int STATUS_DRAW_STOKE = 7;

	/**
	 * �Ŵ󾵵İ뾶
	 */
	private int mPreviewRadius = 100;
	/**
	 * �Ŵ���
	 */
	private final int mFactor = 1;
	/**
	 * ��������
	 */
	private static final float TOUCH_TOLERANCE = 4;
	/**
	 * �ʴ���С
	 */
	private static float PAINT_STROKEWIDTH = 30;
	/**
	 * ��ǰ�ʴ����ű���
	 */
	private float mStrokeMultiples = 1L;
	/**
	 * ���ڶ�ͼƬ�����ƶ������ű任�ľ���
	 */
	private Matrix mMatrix = new Matrix();

	/**
	 * ��չʾ��Bitmap����
	 */
	public Bitmap mSourceBitmap;

	public Bitmap mSourceBitmapBackground;

	/**
	 * ��¼��ǰ������״̬����ѡֵΪSTATUS_INIT��STATUS_ZOOM_OUT��STATUS_ZOOM_IN��STATUS_MOVE
	 */
	private int mCurrentStatus;

	/**
	 * ZoomImageView�ؼ��Ŀ���
	 */
	private int mViewWidth;

	/**
	 * ZoomImageView�ؼ��ĸ߶�
	 */
	private int mViewHeight;

	/**
	 * ��¼��ָͬʱ������Ļ��ʱ�����ĵ�ĺ�����ֵ
	 */
	private float mTwoFingerCenterPointX;

	/**
	 * ��¼��ָͬʱ������Ļ��ʱ�����ĵ��������ֵ
	 */
	private float mTwoFingerCenterPointY;

	/**
	 * ��¼��ǰͼƬ�Ŀ��ȣ�ͼƬ������ʱ�����ֵ��һ��䶯
	 */
	private float mCurrentBitmapWidth;

	/**
	 * ��¼��ǰͼƬ�ĸ߶ȣ�ͼƬ������ʱ�����ֵ��һ��䶯
	 */
	private float mCurrentBitmapHeight;

	/**
	 * ��¼�ϴ���ָ�ƶ�ʱ�ĺ�����
	 */
	private float mLastXMove = -1;

	/**
	 * ��¼�ϴ���ָ�ƶ�ʱ��������
	 */
	private float mLastYMove = -1;

	/**
	 * ��¼��ָ�ں����귽���ϵ��ƶ�����
	 */
	private float mMovedDistanceX;

	/**
	 * ��¼��ָ�������귽���ϵ��ƶ�����
	 */
	private float mMovedDistanceY;

	/**
	 * ��¼ͼƬ�ھ����ϵĺ���ƫ��ֵ
	 */
	private float mTotalTranslateX;

	/**
	 * ��¼ͼƬ�ھ����ϵ�����ƫ��ֵ
	 */
	private float mTotalTranslateY;

	/**
	 * ��¼ͼƬ�ھ����ϵ������ű���
	 */
	private float mTotalRatio;

	/**
	 * ��¼��ָ�ƶ��ľ�������ɵ����ű���
	 */
	private float mScaledRatio;

	/**
	 * ��¼ͼƬ��ʼ��ʱ�����ű���
	 */
	private float mInitRatio;

	/**
	 * ��¼�ϴ���ָ֮��ľ���
	 */
	private double mTwoFingerLastDis;

	/**
	 * ��ǰ��ָ�����X������
	 */
	private float mCurrentX;
	/**
	 * ��ǰ��ָ�����Y������
	 */
	private float mCurrentY;
	/**
	 * ��ǰ�ֲ���ʾͼ�Ƿ������
	 */
	private boolean IsPreviewLeft = true;

	/**
	 * ǰ��ͼ���ư�
	 */
	private Canvas mSourceCanvas;
	/**
	 * �滭��
	 */
	private Paint mSourcePaint;
	private Path mTouchPath;
	/**
	 * �ƶ�ʱ���λ�����bitmap��X������
	 */
	private float mX;
	/**
	 * �ƶ�ʱ���λ�����bitmap��Y������
	 */
	private float mY;

	public MosaicView(Context context) {
		this(context, null);
	}

	public MosaicView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public MosaicView(Context context, AttributeSet attrs, int style) {
		super(context, attrs, style);
		init(context);
	}

	private void init(Context context) {
		mPreviewRadius = DensityUtil.dip2px(getContext(), 50f);
		PAINT_STROKEWIDTH = DensityUtil.dip2px(getContext(), 12f);
		mSourcePaint = new Paint();
		mSourcePaint.setAlpha(0);
		mSourcePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));// ȡ������ƽ�������ʾ�ϲ㡣
		mSourcePaint.setAntiAlias(true);

		mSourcePaint.setDither(true);
		mSourcePaint.setStyle(Paint.Style.STROKE);
		mSourcePaint.setStrokeJoin(Paint.Join.ROUND);
		mSourcePaint.setStrokeCap(Paint.Cap.ROUND);
		mTouchPath = new Path();
	}

	public void setSourceBitmap(Bitmap bitmap) {
		mCurrentStatus = STATUS_INIT;
		bitmap = zoomImage(bitmap, DensityUtil.getDisplayWidth(getContext()),
				DensityUtil.getDisplayHeight(getContext()));

		mSourceBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		mSourceBitmapBackground = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		mSourceCanvas = new Canvas(mSourceBitmap);
		mSourceCanvas.drawBitmap(bitmap, 0, 0, null);

		Canvas canvas = new Canvas(mSourceBitmapBackground);
		canvas.drawBitmap(bitmap, 0, 0, null);
		bitmap.recycle();
		invalidate();
	}

	public void release() {
		if (mSourceBitmap != null && !mSourceBitmap.isRecycled())
			mSourceBitmap.recycle();
		if (mSourceBitmapBackground != null && !mSourceBitmapBackground.isRecycled())
			mSourceBitmapBackground.recycle();
		destroyDrawingCache();
	}

	public void revocation(Bitmap bitmap) {
		mSourceBitmap.recycle();
		mSourceBitmap = null;

		bitmap = zoomImage(bitmap, DensityUtil.getDisplayWidth(getContext()),
				DensityUtil.getDisplayHeight(getContext()));
		mSourceBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		mSourceCanvas = new Canvas(mSourceBitmap);
		mSourceCanvas.drawBitmap(bitmap, 0, 0, null);
		bitmap.recycle();

		mCurrentStatus = STATUS_ACTION_UP;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			// �ֱ��ȡ��ZoomImageView�Ŀ��Ⱥ͸߶�
			mViewWidth = getWidth();
			mViewHeight = getHeight();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_POINTER_DOWN:
			if (event.getPointerCount() == 2) {
				// ����������ָ������Ļ��ʱ��������ָ֮��ľ���
				mTwoFingerLastDis = distanceBetweenFingers(event);
				isMultiTouch = true;
			}
			break;
		case MotionEvent.ACTION_DOWN:
			if (event.getPointerCount() == 1) {
				touchDown((event.getX() - mTotalTranslateX) / mTotalRatio,
						(event.getY() - mTotalTranslateY) / mTotalRatio);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getPointerCount() == 1 && !isMultiTouch) {
				// ֻ�е�ָ������Ļ���ƶ�ʱ��Ϊ�鿴�ֲ�״̬
				mCurrentStatus = STATUS_PART;
				mCurrentX = event.getX();
				mCurrentY = event.getY();
				touchMove((mCurrentX - mTotalTranslateX) / mTotalRatio, (mCurrentY - mTotalTranslateY) / mTotalRatio);
				invalidate();
			} else if (event.getPointerCount() == 2) {
				// �϶�
				float xMove = (event.getX(0) + event.getX(1)) / 2;
				float yMove = (event.getY(0) + event.getY(1)) / 2;

				if (mLastXMove == -1 && mLastYMove == -1) {
					centerMovePointBetweenFingers(event);
				}
				mMovedDistanceX = xMove - mLastXMove;
				mMovedDistanceY = yMove - mLastYMove;
				// ���б߽��飬��������ͼƬ�ϳ��߽�
				if (mTotalTranslateX + mMovedDistanceX > 0) {
					mMovedDistanceX = 0;
				} else if (mViewWidth - (mTotalTranslateX + mMovedDistanceX) > mCurrentBitmapWidth) {
					mMovedDistanceX = 0;
				}
				if (mTotalTranslateY + mMovedDistanceY > 0) {
					mMovedDistanceY = 0;
				} else if (mViewHeight - (mTotalTranslateY + mMovedDistanceY) > mCurrentBitmapHeight) {
					mMovedDistanceY = 0;
				}

				// ����
				Boolean isDrag = false;
				centerPointBetweenFingers(event);
				double fingerDis = distanceBetweenFingers(event);
				if (fingerDis > mTwoFingerLastDis) {
					mCurrentStatus = STATUS_ZOOM_OUT_AND_MOVE;
				} else {
					mCurrentStatus = STATUS_ZOOM_IN_AND_MOVE;
				}
				// �������ű�����飬���ֻ������ͼƬ�Ŵ�4������С������С����ʼ������
				if ((mCurrentStatus == STATUS_ZOOM_OUT_AND_MOVE && mTotalRatio < 4 * mInitRatio)
						|| (mCurrentStatus == STATUS_ZOOM_IN_AND_MOVE && mTotalRatio > mInitRatio)) {
					mScaledRatio = (float) (fingerDis / mTwoFingerLastDis);
					mTotalRatio = mTotalRatio * mScaledRatio;
					if (mTotalRatio > 4 * mInitRatio) {
						mTotalRatio = 4 * mInitRatio;
					} else if (mTotalRatio < mInitRatio) {
						mTotalRatio = mInitRatio;
					}

					isDrag = true;
				} else {
					mCurrentStatus = STATUS_MOVE;
				}

				// ����onDraw()��������ͼƬ
				invalidate();
				if (isDrag) {
					mTwoFingerLastDis = fingerDis;
				}
				centerMovePointBetweenFingers(event);
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			if (event.getPointerCount() == 2) {
				mCurrentStatus = STATUS_ACTION_UP;
				invalidate();
				// ��ָ�뿪��Ļʱ����ʱֵ��ԭ
				mLastXMove = -1;
				mLastYMove = -1;
			}
			break;
		case MotionEvent.ACTION_UP:
			isMultiTouch = false;
			// ��ָ�뿪��Ļʱ����ʱֵ��ԭ
			mCurrentStatus = STATUS_ACTION_UP;
			touchUp();
			invalidate();
			mLastXMove = -1;
			mLastYMove = -1;
			break;
		default:
			break;
		}
		return true;
	}

	private boolean isMultiTouch;

	/**
	 * ����currentStatus��ֵ��������ͼƬ����ʲô���Ļ��Ʋ�����
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		if (mSourceBitmap == null)
			return;
		super.onDraw(canvas);
		switch (mCurrentStatus) {
		case STATUS_PART:
			part(canvas);
			break;
		case STATUS_ZOOM_OUT_AND_MOVE:
		case STATUS_ZOOM_IN_AND_MOVE:
			move(canvas);
			zoom(canvas);
			break;
		case STATUS_MOVE:
			move(canvas);
			break;
		case STATUS_INIT:
			initBitmap(canvas);
			replyPosition(canvas);
			// ���½���ͼƬ
			break;
		case STATUS_ACTION_UP:
			replyPosition(canvas);
			break;
		case STATUS_DRAW_STOKE:
			drawStrokeSize(canvas);
			break;
		default:
			canvas.drawBitmap(mSourceBitmap, mMatrix, null);
			break;
		}
	}

	private void touchDown(float x, float y) {
		mTouchPath.reset();
		mTouchPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touchMove(float x, float y) {
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mTouchPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
			mX = x;
			mY = y;
		}
	}

	private void touchUp() {
		// mPath.lineTo(mX, mY);
		// commit the path to our offscreen
		mSourceCanvas.drawPath(mTouchPath, mSourcePaint);
		// kill this so we don't double draw
		mTouchPath.reset();
	}

	/**
	 * �ֲ�ͼƬ���ڵĻ���
	 * 
	 * @param canvas
	 */
	private void part(Canvas canvas) {
		float bitmapTop = mTotalTranslateY;// bitmap����Y��ֵ
		float bitmapBottom = bitmapTop + mSourceBitmap.getHeight() * mTotalRatio;// bitmap�ײ�Y��ֵ
		float bitmapLeft = mTotalTranslateX;// bitmap��X��ֵ
		float bitmapRight = bitmapLeft + mSourceBitmap.getWidth() * mTotalRatio;// bitmap�Ҳ�X��ֵ

		float circleCenterX = mCurrentX;// ָʾ��Բ�ĵ�X������
		float circleCenterY = mCurrentY;// ָʾ��Բ�ĵ�Y������

		float partCenterX = mCurrentX;// �ֲ�ͼ���ĵ�X������
		float partCenterY = mCurrentY;// �ֲ�ͼ���ĵ�Y������

		if (mCurrentX < (mPreviewRadius * 2) && mCurrentY < (mPreviewRadius * 2) && IsPreviewLeft) {
			IsPreviewLeft = false;
		} else if ((mCurrentX > canvas.getWidth() - (mPreviewRadius * 2) && mCurrentY < (mPreviewRadius * 2))
				&& !IsPreviewLeft) {
			IsPreviewLeft = true;
		}

		if ((mCurrentY < bitmapTop + mPreviewRadius) || (mCurrentY > bitmapBottom - mPreviewRadius)
				|| (mCurrentX < bitmapLeft + mPreviewRadius) || (mCurrentX > bitmapRight - mPreviewRadius)) {
			if ((mCurrentY < bitmapTop + mPreviewRadius)) {// ׼���Ϸ�Խ��
				partCenterY = mCurrentY + (bitmapTop + mPreviewRadius - mCurrentY);
			}
			if ((mCurrentY > bitmapBottom - mPreviewRadius)) {// ׼���·�Խ��
				partCenterY = mCurrentY - (mCurrentY - bitmapBottom + mPreviewRadius);
			}
			if (mCurrentX < bitmapLeft + mPreviewRadius) {// ׼����Խ��
				partCenterX = mCurrentX + (bitmapLeft + mPreviewRadius - mCurrentX);
			}
			if (mCurrentX > bitmapRight - mPreviewRadius) {// ׼���ҷ�Խ��
				partCenterX = mCurrentX - (mCurrentX - bitmapRight + mPreviewRadius);
			}
		}
		Path path = new Path();
		path.addRect(0, 0, mPreviewRadius * 2, mPreviewRadius * 2, Direction.CW);
		// ��ͼ
		canvas.drawBitmap(mSourceBitmapBackground, mMatrix, null);
		canvas.drawBitmap(mSourceBitmap, mMatrix, null);
		mSourceCanvas.drawPath(mTouchPath, mSourcePaint);

		// ����
		if (IsPreviewLeft) {
			canvas.translate(0, 0);
		} else {
			canvas.translate(canvas.getWidth() - mPreviewRadius * 2, 0);
		}
		canvas.clipPath(path);
		// ���ֲ�ͼ
		canvas.translate(mPreviewRadius - partCenterX * mFactor, mPreviewRadius - partCenterY * mFactor);
		canvas.drawBitmap(mSourceBitmapBackground, mMatrix, null);
		canvas.drawBitmap(mSourceBitmap, mMatrix, null);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		// ����ָʾ���м�
		paint.setColor(getResources().getColor(R.color.mosaicdark));
		paint.setStyle(Style.FILL);// ʵ��ͼ��
		if (mInitRatio > 1) {
			canvas.drawCircle(circleCenterX, circleCenterY, (PAINT_STROKEWIDTH - 5) / 2 * mInitRatio * mStrokeMultiples,
					paint);
		} else {
			canvas.drawCircle(circleCenterX, circleCenterY, (PAINT_STROKEWIDTH - 5) / 2 / mInitRatio * mStrokeMultiples,
					paint);
		}
		// ���ƻ���ָʾ��ԭ�α߿�
		paint.setColor(getResources().getColor(R.color.mosaicblue));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(4f);
		if (mTotalRatio > 1) {
			canvas.drawCircle(circleCenterX, circleCenterY, (PAINT_STROKEWIDTH - 4) / 2 * mInitRatio * mStrokeMultiples,
					paint);
		} else {
			canvas.drawCircle(circleCenterX, circleCenterY, (PAINT_STROKEWIDTH - 4) / 2 / mInitRatio * mStrokeMultiples,
					paint);
		}

		// ���ư�ɫ�߿�
		paint.setStyle(Style.STROKE);// ����ͼ��
		paint.setStrokeWidth(2.5f);
		paint.setColor(Color.WHITE);
		canvas.drawRect(new RectF(partCenterX - mPreviewRadius + 1, partCenterY - mPreviewRadius + 1,
				partCenterX + mPreviewRadius - 1, partCenterY + mPreviewRadius - 1), paint);

	}

	/**
	 * ��ͼƬ�������Ŵ�����
	 * 
	 * @param canvas
	 */
	private void zoom(Canvas canvas) {
		mSourcePaint.setStrokeWidth(PAINT_STROKEWIDTH / mTotalRatio * mStrokeMultiples);
		mMatrix.reset();
		// ��ͼƬ�������ű�����������
		mMatrix.postScale(mTotalRatio, mTotalRatio);
		float scaledWidth = mSourceBitmap.getWidth() * mTotalRatio;
		float scaledHeight = mSourceBitmap.getHeight() * mTotalRatio;
		float translateX = 0f;
		float translateY = 0f;
		// �����ǰͼƬ����С����Ļ���ȣ�����Ļ���ĵĺ��������ˮƽ���š�������ָ�����ĵ�ĺ��������ˮƽ����
		if (mCurrentBitmapWidth < mViewWidth) {
			translateX = (mViewWidth - scaledWidth) / 2f;
		} else {
			translateX = mTotalTranslateX * mScaledRatio + mTwoFingerCenterPointX * (1 - mScaledRatio);
			// ���б߽��飬��֤ͼƬ���ź���ˮƽ�����ϲ���ƫ�Ƴ���Ļ
			if (translateX > 0) {
				translateX = 0;
			} else if (mViewWidth - translateX > scaledWidth) {
				translateX = mViewWidth - scaledWidth;
			}
		}
		// �����ǰͼƬ�߶�С����Ļ�߶ȣ�����Ļ���ĵ���������д�ֱ���š�������ָ�����ĵ����������д�ֱ����
		if (mCurrentBitmapHeight < mViewHeight) {
			translateY = (mViewHeight - scaledHeight) / 2f;
		} else {
			translateY = mTotalTranslateY * mScaledRatio + mTwoFingerCenterPointY * (1 - mScaledRatio);
			// ���б߽��飬��֤ͼƬ���ź��ڴ�ֱ�����ϲ���ƫ�Ƴ���Ļ
			if (translateY > 0) {
				translateY = 0;
			} else if (mViewHeight - translateY > scaledHeight) {
				translateY = mViewHeight - scaledHeight;
			}
		}
		// ���ź��ͼƬ����ƫ�ƣ��Ա�֤���ź����ĵ�λ�ò���
		mMatrix.postTranslate(translateX, translateY);
		mTotalTranslateX = translateX;
		mTotalTranslateY = translateY;
		mCurrentBitmapWidth = scaledWidth;
		mCurrentBitmapHeight = scaledHeight;
		canvas.drawBitmap(mSourceBitmapBackground, mMatrix, null);
		canvas.drawBitmap(mSourceBitmap, mMatrix, null);
	}

	/**
	 * ��ͼƬ����ƽ�ƴ���
	 * 
	 * @param canvas
	 */
	private void move(Canvas canvas) {
		mMatrix.reset();
		// ������ָ�ƶ��ľ���������ƫ��ֵ
		float translateX = mTotalTranslateX + mMovedDistanceX;
		float translateY = mTotalTranslateY + mMovedDistanceY;
		// �Ȱ������е����ű�����ͼƬ��������
		mMatrix.postScale(mTotalRatio, mTotalRatio);
		// �ٸ����ƶ��������ƫ��
		mMatrix.postTranslate(translateX, translateY);
		mTotalTranslateX = translateX;
		mTotalTranslateY = translateY;
		canvas.drawBitmap(mSourceBitmapBackground, mMatrix, null);
		canvas.drawBitmap(mSourceBitmap, mMatrix, null);
	}

	/**
	 * ��ͼƬ���г�ʼ��������������ͼƬ���У��Լ���ͼƬ������Ļ����ʱ��ͼƬ����ѹ����
	 * 
	 * @param canvas
	 */
	private void initBitmap(Canvas canvas) {
		if (mSourceBitmap != null) {
			mMatrix.reset();
			int bitmapWidth = mSourceBitmap.getWidth();
			int bitmapHeight = mSourceBitmap.getHeight();
			if (bitmapWidth > mViewWidth || bitmapHeight > mViewHeight) {
				if (bitmapWidth - mViewWidth > bitmapHeight - mViewHeight) {
					// ��ͼƬ���ȴ�����Ļ����ʱ����ͼƬ�ȱ���ѹ����ʹ��������ȫ��ʾ����
					float ratio = mViewWidth / (bitmapWidth * 1.0f);
					mMatrix.postScale(ratio, ratio);
					float translateY = (mViewHeight - (bitmapHeight * ratio)) / 2f;
					// �������귽���Ͻ���ƫ�ƣ��Ա�֤ͼƬ������ʾ
					mMatrix.postTranslate(0, translateY);
					mTotalTranslateY = translateY;
					mTotalRatio = mInitRatio = ratio;
				} else {
					// ��ͼƬ�߶ȴ��ڿؼ��߶�ʱ����ͼƬ�ȱ���ѹ����ʹ��������ȫ��ʾ����
					float ratio = mViewHeight / (bitmapHeight * 1.0f);
					mMatrix.postScale(ratio, ratio);
					float translateX = (mViewWidth - (bitmapWidth * ratio)) / 2f;
					// �ں����귽���Ͻ���ƫ�ƣ��Ա�֤ͼƬ������ʾ
					mMatrix.postTranslate(translateX, 0);
					mTotalTranslateX = translateX;
					mTotalRatio = mInitRatio = ratio;
				}
				mCurrentBitmapWidth = bitmapWidth * mInitRatio;
				mCurrentBitmapHeight = bitmapHeight * mInitRatio;
			} else {

				// ��ͼƬ�Ŀ��߶�С����Ļ����ʱ��ֱ���÷Ŵ���һ������Ϊֹ
				float ratio = 0;
				if ((mViewWidth / (bitmapWidth * 1.0f)) > (mViewHeight / (bitmapHeight * 1.0f))) {
					ratio = mViewHeight / (bitmapHeight * 1.0f);
				} else {
					ratio = mViewWidth / (bitmapWidth * 1.0f);
				}
				mMatrix.postScale(mTotalRatio, mTotalRatio);

				float translateY = (mViewHeight - (bitmapHeight * ratio)) / 2f;
				float translateX = (mViewWidth - (bitmapWidth * ratio)) / 2f;
				mMatrix.postTranslate(translateX, translateY);
				mTotalRatio = mInitRatio = ratio;
				mTotalTranslateX = translateX;
				mTotalTranslateY = translateY;
				mCurrentBitmapWidth = bitmapWidth * mInitRatio;
				;
				mCurrentBitmapHeight = bitmapHeight * mInitRatio;
				;
			}
			System.out.println("===================" + mTotalRatio);
			/*
			 * color = new
			 * int[sourceBitmapCopy.getWidth()][sourceBitmapCopy.getHeight()];
			 * newColor = new
			 * int[sourceBitmapCopy.getWidth()][sourceBitmapCopy.getHeight()];
			 * for (int y = 0; y < sourceBitmapCopy.getHeight(); y++) { for (int
			 * x = 0; x < sourceBitmapCopy.getWidth(); x++) { color[x][y] =
			 * sourceBitmapCopy.getPixel(x, y); } } newColor(newColor, color);
			 * for (int x = 0; x < sourceBitmapCopy.getWidth(); x++) { for (int
			 * y = 0; y < sourceBitmapCopy.getHeight(); y++) {
			 * sourceBitmapCopy.setPixel(x, y, newColor[x][y]); } }
			 */
			mSourceBitmapBackground = getMosaic(mSourceBitmapBackground);
			canvas.drawBitmap(mSourceBitmapBackground, mMatrix, null);
			canvas.drawBitmap(mSourceBitmap, mMatrix, null);
			mSourcePaint.setStrokeWidth(PAINT_STROKEWIDTH / mTotalRatio * mStrokeMultiples);
		}
	}

	/**
	 * ���Ʊʴ���С
	 * 
	 * @param mStrokeMultiples
	 * @param canvas
	 */
	public void drawStrokeSize(Canvas canvas) {
		// TODO Auto-generated method stub
		// ��ͼ
		canvas.drawBitmap(mSourceBitmapBackground, mMatrix, null);
		canvas.drawBitmap(mSourceBitmap, mMatrix, null);
		mSourceCanvas.drawPath(mTouchPath, mSourcePaint);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		// ���ƻ���ָʾ��ԭ�α߿�
		paint.setColor(getResources().getColor(R.color.mosaicblue));
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(4f);
		if (mTotalRatio > 1) {
			canvas.drawCircle(mViewWidth / 2, mViewHeight / 2,
					(PAINT_STROKEWIDTH - 4) / 2 * mInitRatio * mStrokeMultiples, paint);
		} else {
			canvas.drawCircle(mViewWidth / 2, mViewHeight / 2,
					(PAINT_STROKEWIDTH - 4) / 2 / mInitRatio * mStrokeMultiples, paint);
		}
	}

	public void setStrokeMultiples(float strokeMultiples) {
		this.mStrokeMultiples = strokeMultiples;
		mSourcePaint.setStrokeWidth(PAINT_STROKEWIDTH / mTotalRatio * strokeMultiples);
		mCurrentStatus = STATUS_DRAW_STOKE;
		invalidate();
	}

	public void removeStrokeView() {
		// TODO Auto-generated method stub
		mCurrentStatus = STATUS_ACTION_UP;
		invalidate();
	}

	/**
	 * ��ͼƬ���лָ�
	 * 
	 * @param canvas
	 */
	private void replyPosition(Canvas canvas) {
		mMatrix.reset();
		// �Ȱ������е����ű�����ͼƬ��������
		mMatrix.postScale(mTotalRatio, mTotalRatio);
		// �ٸ����ƶ��������ƫ��
		mMatrix.postTranslate(mTotalTranslateX, mTotalTranslateY);
		canvas.drawBitmap(mSourceBitmapBackground, mMatrix, null);
		canvas.drawBitmap(mSourceBitmap, mMatrix, null);
	}

	/**
	 * ����������ָ֮��ľ��롣
	 * 
	 * @param event
	 * @return ������ָ֮��ľ���
	 */
	private double distanceBetweenFingers(MotionEvent event) {
		float disX = Math.abs(event.getX(0) - event.getX(1));
		float disY = Math.abs(event.getY(0) - event.getY(1));
		return Math.sqrt(disX * disX + disY * disY);
	}

	/**
	 * ����������ָ֮�����ĵ�����ꡣ
	 * 
	 * @param event
	 */
	private void centerPointBetweenFingers(MotionEvent event) {
		float xPoint0 = event.getX(0);
		float yPoint0 = event.getY(0);
		float xPoint1 = event.getX(1);
		float yPoint1 = event.getY(1);
		mTwoFingerCenterPointX = (xPoint0 + xPoint1) / 2;
		mTwoFingerCenterPointY = (yPoint0 + yPoint1) / 2;
	}

	/**
	 * �����ƶ�ʱ������ָ֮�����ĵ�����ꡣ
	 * 
	 * @param event
	 */
	private void centerMovePointBetweenFingers(MotionEvent event) {
		float xPoint0 = event.getX(0);
		float yPoint0 = event.getY(0);
		float xPoint1 = event.getX(1);
		float yPoint1 = event.getY(1);
		mLastXMove = (xPoint0 + xPoint1) / 2;
		mLastYMove = (yPoint0 + yPoint1) / 2;
	}

	/**
	 * �ϲ�����bitmapΪһ��
	 * 
	 * @param background
	 * @param foreground
	 * @return Bitmap
	 */
	public Bitmap getMosaicBitmap() {
		if (mSourceBitmapBackground == null) {
			return null;
		}
		int bgWidth = mSourceBitmapBackground.getWidth();
		int bgHeight = mSourceBitmapBackground.getHeight();
		int fgWidth = mSourceBitmap.getWidth();
		int fgHeight = mSourceBitmap.getHeight();
		Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
		Canvas canvas = new Canvas(newmap);
		canvas.drawBitmap(mSourceBitmapBackground, 0, 0, null);
		canvas.drawBitmap(mSourceBitmap, (bgWidth - fgWidth) / 2, (bgHeight - fgHeight) / 2, null);
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return newmap;
	}

	/**
	 * ������Ч��(Native)
	 * 
	 * @param bitmap
	 *            ԭͼ
	 * 
	 * @return ������ͼƬ
	 * 
	 */
	public static Bitmap getMosaic(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int radius = width / 30;

		Bitmap mosaicBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(mosaicBitmap);

		int horCount = (int) Math.ceil(width / (float) radius);
		int verCount = (int) Math.ceil(height / (float) radius);

		Paint paint = new Paint();
		paint.setAntiAlias(true);

		for (int horIndex = 0; horIndex < horCount; ++horIndex) {
			for (int verIndex = 0; verIndex < verCount; ++verIndex) {
				int l = radius * horIndex;
				int t = radius * verIndex;
				int r = l + radius;
				if (r > width) {
					r = width;
				}
				int b = t + radius;
				if (b > height) {
					b = height;
				}
				int color = bitmap.getPixel(l, t);
				Rect rect = new Rect(l, t, r, b);
				paint.setColor(color);
				canvas.drawRect(rect, paint);
			}
		}
		canvas.save();

		return mosaicBitmap;
	}

	/***
	 * ͼƬ�����ŷ���
	 * 
	 * @param bgimage
	 *            ��ԴͼƬ��Դ
	 * @param newWidth
	 *            �����ź����
	 * @param newHeight
	 *            �����ź�߶�
	 * @return
	 */
	public Bitmap zoomImage(Bitmap bgimage, int newWidth, int newHeight) {
		// ��ȡ���ͼƬ�Ŀ��͸�
		int width = bgimage.getWidth();
		int height = bgimage.getHeight();
		// ִ�и����ŷ����������У���һ��С����Ļ�Ĵ�С����ͼƬ�ĸ߶ȴ��ڳ���
		double x = width * newHeight;
		double y = height * newWidth;

		if (x > y) {
			newHeight = (int) (y / (double) width);
		} else if (x < y) {
			newWidth = (int) (x / (double) height);
		}

		if (newWidth > width && newHeight > height) {
			newWidth = width;
			newHeight = height;
		}
		Matrix matrix = new Matrix();
		matrix.reset();
		// �������������
		float scaleWidth = ((float) newWidth) / (float) width;
		float scaleHeight = ((float) newHeight) / (float) height;
		matrix.postScale(scaleWidth, scaleHeight);

		bgimage = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
		return bgimage;
	}

}