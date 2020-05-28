package edu.columbia.lrl.CrossLayer.physical_models.util;

import ch.epfl.general_libraries.clazzes.ParamName;


public class ChannelsAndBandwidthFormat extends AbstractLinkFormat {

	private double totalRate;
	private int channels;

	private final double channelRate;

	public ChannelsAndBandwidthFormat(@ParamName(name = "Number of channels") int channels,
			@ParamName(name = "Total rate in Gb/s") double totalRate) {
		this.totalRate = totalRate;
		this.channels = channels;
		this.channelRate = totalRate * 1.0E9D / (double) channels;
	}

	public double getWavelengthRate() {
		return channelRate;
	}

	public int getNumberOfChannels() {
		return this.channels;
	}

	public boolean isNumberOfChannelFixed() {
		return this.channels > 0;
	}

	@Override
	public double getAggregateRateInGbs() {
		return totalRate;
	}

	@Override
	public void setNumberOfWavelengths(int channels) {
		this.channels = channels;
	}
}
