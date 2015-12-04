package com.leontg77.uhc.worlds.orelimiter;

import java.util.Objects;

/**
 * Ore location class.
 * 
 * @author dans1988, modified by LeonTG77.
 */
public class OreLocation {
	private final int x;
	private final int y;
	private final int z;

	private final int hashCode;

	public OreLocation(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;

		hashCode = Objects.hash(x, y, z);
	}

	/**
	 * Get the X coord of the OreLocation.
	 * 
	 * @return The X coord.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the Y coord of the OreLocation.
	 * 
	 * @return The Y coord.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get the Z coord of the OreLocation.
	 * 
	 * @return The Z coord.
	 */
	public int getZ() {
		return z;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof OreLocation)) {
			return false;
		}

		OreLocation other = (OreLocation) obj;

		return Objects.equals(this.x, other.x) && Objects.equals(this.y, other.y) && Objects.equals(this.z, other.z);
	}
}