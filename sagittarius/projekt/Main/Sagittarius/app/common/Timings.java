package common;

import models.*;

/**
 *
 * @author johan
 */
public class Timings {

	public static double[] getExtremes(long stageID, long weaponCategoryID) {
		double[] result = null;

		Stage stage = Stage.findById(stageID);
		WeaponCategory weaponCategory = WeaponCategory.findById(weaponCategoryID);
		if (stage != null && weaponCategory != null) {
			double minTime = 1000;
			double maxTime = 0;
			double targetTime = 0;
			int targets = 0;
			int movements = 0;

			try {
				for (TargetGroup targetGroup : stage.targetGroups) {
					for (Target target : targetGroup.targets) {
						String classification = String.format("%d", target.targetShape.classification);
						TargetClass targetClass = TargetClass.find("byClassificationAndWeaponCategory", classification, weaponCategory).first();
						if (targetClass != null) {
							double time = weaponCategory.shotInterval * ((double) targetGroup.range / (double) targetClass.maximumRange);
							minTime = Math.min(time, minTime);
							maxTime = Math.max(time, maxTime);
							targetTime += time;
							targets++;
						}
					}
					movements++;
				}

				minTime = 1.0 + targetTime + (minTime * (6 - targets)) + 1.0 * (movements - 1);
				maxTime = 1.5 + targetTime + (maxTime * (6 - targets)) + 1.5 * (movements - 1);

				result = new double[]{minTime, maxTime};

			} catch (Exception e) {
			}
		}
		return result;
	}
}